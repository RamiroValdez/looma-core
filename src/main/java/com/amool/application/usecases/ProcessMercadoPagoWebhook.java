package com.amool.application.usecases;

import com.amool.application.port.out.*;
import com.amool.domain.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class ProcessMercadoPagoWebhook {

    private final RestTemplate restTemplate;
    private final PaymentAuditPort paymentAuditPort;
    private final UserBalancePort userBalancePort;
    private final PaymentRecordPort paymentRecordPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final LoadChapterPort loadChapterPort;
    private final SubscribeUser subscribeUser;
    private final PaymentSessionLinkPort paymentSessionLinkPort;
    private final LoadUserPort loadUserPort;

    @Value("${payments.mercadopago.accessToken}")
    private String accessToken;

    @Value("${payments.mercadopago.apiBase:https://api.mercadopago.com}")
    private String apiBase;

    public ProcessMercadoPagoWebhook(RestTemplate restTemplate,
                                     PaymentAuditPort paymentAuditPort,
                                     UserBalancePort userBalancePort,
                                     PaymentRecordPort paymentRecordPort,
                                     ObtainWorkByIdPort obtainWorkByIdPort,
                                     LoadChapterPort loadChapterPort,
                                     SubscribeUser subscribeUser,
                                     PaymentSessionLinkPort paymentSessionLinkPort,
                                     LoadUserPort loadUserPort) {
        this.restTemplate = restTemplate;
        this.paymentAuditPort = paymentAuditPort;
        this.userBalancePort = userBalancePort;
        this.paymentRecordPort = paymentRecordPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadChapterPort = loadChapterPort;
        this.subscribeUser = subscribeUser;
        this.paymentSessionLinkPort = paymentSessionLinkPort;
        this.loadUserPort = loadUserPort;
    }

    public ProcessMercadoPagoWebhookResult execute(String paymentId, String externalReference, Map<?, ?> paymentData) {
        if (accessToken == null || accessToken.isBlank()) {
            return ProcessMercadoPagoWebhookResult.error("MercadoPago access token is not configured");
        }

        Map<?, ?> payment = paymentData;
        if (payment == null && paymentId != null) {
            try {
                payment = fetchPaymentFromApi(paymentId);
            } catch (Exception e) {
                return ProcessMercadoPagoWebhookResult.error("Error fetching payment from MercadoPago API");
            }
        }

        if (payment == null || !isPaymentApproved(payment)) {
            return ProcessMercadoPagoWebhookResult.ignored("Payment not approved or not found");
        }

        if (externalReference == null) {
            Object externalRefObj = payment.get("external_reference");
            externalReference = externalRefObj == null ? null : externalRefObj.toString();
        }

        if (externalReference == null || externalReference.isBlank()) {
            return ProcessMercadoPagoWebhookResult.error("Payment missing external_reference");
        }

        DecodedRef ref;
        try {
            ref = decodeExternalReference(externalReference);
        } catch (IllegalArgumentException ex) {
            return ProcessMercadoPagoWebhookResult.error("Invalid externalReference format: " + ex.getMessage());
        }

        String finalPaymentId = paymentId != null ? paymentId : "manual-" + System.currentTimeMillis();

        boolean firstTime = paymentAuditPort.markProcessedIfFirst(finalPaymentId);
        if (firstTime) {
            processAuthorPayout(payment, ref);
        }

        savePaymentRecord(finalPaymentId, payment, ref, externalReference);

        subscribeUser.execute(ref.userId, ref.type, ref.targetId);

        return ProcessMercadoPagoWebhookResult.success();
    }

    private Map<?, ?> fetchPaymentFromApi(String paymentId) {
        String url = apiBase + "/v1/payments/" + paymentId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> req = new HttpEntity<>(headers);
        var resp = restTemplate.exchange(url, HttpMethod.GET, req, Map.class);
        return resp.getBody();
    }

    private boolean isPaymentApproved(Map<?, ?> payment) {
        Object statusObj = payment.get("status");
        String status = statusObj == null ? null : statusObj.toString();
        return status != null && status.equalsIgnoreCase("approved");
    }

    private void processAuthorPayout(Map<?, ?> payment, DecodedRef ref) {
        try {
            BigDecimal amountPaid = extractTransactionAmount(payment);
            if (amountPaid == null) {
                amountPaid = resolveAmount(ref);
            }
            
            Long authorId = resolveAuthorId(ref);
            if (amountPaid != null && authorId != null && amountPaid.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal payout = amountPaid.multiply(new BigDecimal("0.92"));
                userBalancePort.addMoney(authorId, payout);
            }
        } catch (Exception ignore) {
        }
    }

    private void savePaymentRecord(String paymentId, Map<?, ?> payment, DecodedRef ref, String externalReference) {
        try {
            PaymentRecord rec = new PaymentRecord();
            rec.setId(paymentId);
            rec.setUserId(ref.userId);
            rec.setProvider("mercadopago");
            rec.setStatus("approved");
            rec.setSubscriptionType(ref.type.name());
            rec.setTargetId(ref.targetId);
            rec.setExternalReference(externalReference);

            String sessionUuid = null;
            if (payment != null) {
                Object metaObj = payment.get("metadata");
                if (metaObj instanceof Map<?, ?> meta) {
                    Object sid = meta.get("session_uuid");
                    if (sid != null) sessionUuid = sid.toString();
                }
            }
            if (sessionUuid == null) {
                String normalized = ref.userId + ":" + ref.type.name().toLowerCase() + ":" + ref.targetId;
                sessionUuid = paymentSessionLinkPort.findSessionUuid(normalized).orElse(null);
            }
            rec.setSessionUuid(sessionUuid);
            
            String method = payment != null && payment.get("payment_method_id") != null ?
                    payment.get("payment_method_id").toString() : null;
            rec.setPaymentMethod(method);
            
            String description = payment != null && payment.get("description") != null ? 
                payment.get("description").toString() : null;
            if (description == null || description.isBlank()) {
                description = buildTitle(ref);
            }
            rec.setTitle(description);
            
            BigDecimal amountPaid = extractTransactionAmount(payment);
            rec.setAmount(amountPaid);

            String currency = payment != null && payment.get("currency_id") != null ?
                    payment.get("currency_id").toString() : null;
            rec.setCurrency(currency);

            OffsetDateTime created = extractApprovedAt(payment);
            if (created == null) {
                created = OffsetDateTime.now();
            }
            rec.setCreatedAt(created);
            
            paymentRecordPort.save(rec);
        } catch (Exception ignore) {
        }
    }

    private DecodedRef decodeExternalReference(String externalRef) {
        if (externalRef == null) {
            throw new IllegalArgumentException("null externalRef");
        }
        
        String s = externalRef.trim();
        if (s.startsWith("<") && s.endsWith(">")) {
            s = s.substring(1, s.length() - 1);
        }
        
        String[] parts = s.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("bad format");
        }
        
        Long userId = Long.valueOf(parts[0]);
        SubscriptionType type = SubscriptionType.fromString(parts[1]);
        Long targetId = Long.valueOf(parts[2]);
        
        return new DecodedRef(userId, type, targetId);
    }

    private BigDecimal resolveAmount(DecodedRef ref) {
        switch (ref.type) {
            case WORK -> {
                Work w = obtainWorkByIdPort.obtainWorkById(ref.targetId)
                        .orElseThrow(() -> new IllegalArgumentException("work not found"));
                if (w.getPrice() == null) return BigDecimal.ZERO;
                return w.getPrice();
            }
            case CHAPTER -> {
                Chapter c = loadChapterPort.loadChapterForEdit(ref.targetId)
                        .orElseThrow(() -> new IllegalArgumentException("chapter not found"));
                if (c.getPrice() == null) return BigDecimal.ZERO;
                return c.getPrice();
            }
            case AUTHOR -> {
                User u = loadUserPort.getById(ref.targetId)
                        .orElseThrow(() -> new IllegalArgumentException("author not found"));
                if (u.getPrice() == null) return BigDecimal.ZERO;
                return u.getPrice();
            }
            default -> {
                return BigDecimal.ZERO;
            }
        }
    }

    private String buildTitle(DecodedRef ref) {
        return switch (ref.type) {
            case WORK -> {
                Work w = obtainWorkByIdPort.obtainWorkById(ref.targetId).orElse(null);
                String wTitle = (w == null || w.getTitle() == null) ? ("work-" + ref.targetId) : w.getTitle();
                yield "Suscripción a " + wTitle;
            }
            case CHAPTER -> {
                Chapter c = loadChapterPort.loadChapterForEdit(ref.targetId).orElse(null);
                String cTitle = (c == null || c.getTitle() == null) ? ("cap-" + ref.targetId) : c.getTitle();
                yield "Suscripción a capítulo " + cTitle;
            }
            case AUTHOR -> {
                yield "Suscripción a autor " + ref.targetId;
            }
        };
    }

    private BigDecimal extractTransactionAmount(Map<?, ?> payment) {
        if (payment == null) return null;
        Object amt = payment.get("transaction_amount");
        if (amt == null) return null;
        try {
            if (amt instanceof Number n) return new BigDecimal(n.toString());
            return new BigDecimal(amt.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private OffsetDateTime extractApprovedAt(Map<?, ?> payment) {
        if (payment == null) return null;
        Object dt = payment.get("date_approved");
        if (dt == null) return null;
        try {
            return OffsetDateTime.parse(dt.toString());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Long resolveAuthorId(DecodedRef ref) {
        switch (ref.type) {
            case WORK -> {
                return obtainWorkByIdPort.obtainWorkById(ref.targetId)
                        .map(w -> w.getCreator() != null ? w.getCreator().getId() : null)
                        .orElse(null);
            }
            case CHAPTER -> {
                Chapter c = loadChapterPort.loadChapterForEdit(ref.targetId).orElse(null);
                if (c == null || c.getWorkId() == null) return null;
                return obtainWorkByIdPort.obtainWorkById(c.getWorkId())
                        .map(w -> w.getCreator() != null ? w.getCreator().getId() : null)
                        .orElse(null);
            }
            case AUTHOR -> {
                return ref.targetId;
            }
            default -> {
                return null;
            }
        }
    }

    private record DecodedRef(Long userId, SubscriptionType type, Long targetId) {}

    public static class ProcessMercadoPagoWebhookResult {
        private final boolean success;
        private final String errorMessage;
        private final boolean ignored;

        private ProcessMercadoPagoWebhookResult(boolean success, String errorMessage, boolean ignored) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.ignored = ignored;
        }

        public static ProcessMercadoPagoWebhookResult success() {
            return new ProcessMercadoPagoWebhookResult(true, null, false);
        }

        public static ProcessMercadoPagoWebhookResult error(String message) {
            return new ProcessMercadoPagoWebhookResult(false, message, false);
        }

        public static ProcessMercadoPagoWebhookResult ignored(String message) {
            return new ProcessMercadoPagoWebhookResult(false, message, true);
        }

        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public boolean isIgnored() { return ignored; }
    }
}
