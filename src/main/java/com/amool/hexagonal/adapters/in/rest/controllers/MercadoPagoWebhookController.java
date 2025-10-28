package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.application.port.in.PaymentService;
import com.amool.hexagonal.domain.model.SubscriptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import com.amool.hexagonal.application.port.out.PaymentAuditPort;
import com.amool.hexagonal.application.port.out.UserBalancePort;
import com.amool.hexagonal.application.port.out.PaymentRecordPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.Chapter;
import com.amool.hexagonal.domain.model.PaymentRecord;

@RestController
@RequestMapping("/api/payments/webhook/mercadopago")
public class MercadoPagoWebhookController {

    private final PaymentService paymentService;
    private final RestTemplate restTemplate;
    private final PaymentAuditPort paymentAuditPort;
    private final UserBalancePort userBalancePort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final LoadChapterPort loadChapterPort;
    private final PaymentRecordPort paymentRecordPort;

    @Value("${payments.mercadopago.accessToken}")
    private String accessToken;

    @Value("${payments.mercadopago.apiBase:https://api.mercadopago.com}")
    private String apiBase;

    @Value("${payments.pricing.author:0}")
    private BigDecimal authorPrice;

    public MercadoPagoWebhookController(PaymentService paymentService,
                                       RestTemplate restTemplate,
                                       PaymentAuditPort paymentAuditPort,
                                       UserBalancePort userBalancePort,
                                       ObtainWorkByIdPort obtainWorkByIdPort,
                                       LoadChapterPort loadChapterPort,
                                       PaymentRecordPort paymentRecordPort) {
        this.paymentService = paymentService;
        this.restTemplate = restTemplate;
        this.paymentAuditPort = paymentAuditPort;
        this.userBalancePort = userBalancePort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadChapterPort = loadChapterPort;
        this.paymentRecordPort = paymentRecordPort;
    }

    @PostMapping
    public ResponseEntity<?> handlePost(@RequestParam(value = "type", required = false) String type,
                                        @RequestParam(value = "topic", required = false) String topic,
                                        @RequestParam(value = "id", required = false) String id,
                                        @RequestBody(required = false) Map<String, Object> body) {
        return process(type, topic, id, body);
    }

    @GetMapping
    public ResponseEntity<?> handleGet(@RequestParam(value = "type", required = false) String type,
                                       @RequestParam(value = "topic", required = false) String topic,
                                       @RequestParam(value = "id", required = false) String id) {
        return process(type, topic, id, null);
    }

    private ResponseEntity<?> process(String type, String topic, String id, Map<String, Object> body) {
        try {
            if (accessToken == null || accessToken.isBlank()) {
                return ResponseEntity.status(500).body("MercadoPago access token is not configured");
            }

            String paymentIdStr = id;
            if (paymentIdStr == null && body != null) {
                Object dataObj = body.get("data");
                if (dataObj instanceof Map<?, ?> dataMap) {
                    Object innerId = dataMap.get("id");
                    if (innerId != null) paymentIdStr = String.valueOf(innerId);
                }
            }
            if (paymentIdStr == null) {
                if (body != null && body.get("externalReference") instanceof String ref) {
                    return handleApprovedPayment("manual-" + System.currentTimeMillis(), ref, null);
                }
                return ResponseEntity.badRequest().body("Missing payment id");
            }

            String url = apiBase + "/v1/payments/" + paymentIdStr;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> req = new HttpEntity<>(headers);
            var resp = restTemplate.exchange(url, HttpMethod.GET, req, Map.class);
            Map<?, ?> payment = resp.getBody();
            if (payment == null) {
                return ResponseEntity.accepted().build();
            }
            Object statusObj = payment.get("status");
            String status = statusObj == null ? null : statusObj.toString();
            if (status == null || !status.equalsIgnoreCase("approved")) {
                return ResponseEntity.noContent().build();
            }

            Object externalRefObj = payment.get("external_reference");
            String externalRef = externalRefObj == null ? null : externalRefObj.toString();
            if (externalRef == null || externalRef.isBlank()) {
                return ResponseEntity.badRequest().body("Payment missing external_reference");
            }
            String paymentId = String.valueOf(payment.get("id"));
            return handleApprovedPayment(paymentId, externalRef, payment);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Webhook processing error");
        }
    }

    private ResponseEntity<?> handleApprovedPayment(String paymentId, String externalRef, Map<?, ?> payment) {
        DecodedRef ref;
        try {
            ref = decodeExternalReference(externalRef);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid externalReference format");
        }

        boolean firstTime = paymentAuditPort.markProcessedIfFirst(paymentId);
        if (firstTime) {
            try {
                BigDecimal amountPaid = extractTransactionAmount(payment);
                if (amountPaid == null) amountPaid = resolveAmount(ref);
                Long authorId = resolveAuthorId(ref);
                if (amountPaid != null && authorId != null && amountPaid.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal payout = amountPaid.multiply(new BigDecimal("0.92"));
                    userBalancePort.addMoney(authorId, payout);
                }
            } catch (Exception ignore) {
            }
        }

        try {
            PaymentRecord rec = new PaymentRecord();
            rec.setId(paymentId);
            rec.setUserId(ref.userId);
            rec.setProvider("mercadopago");
            rec.setStatus("approved");
            rec.setSubscriptionType(ref.type.name());
            rec.setTargetId(ref.targetId);
            BigDecimal amountPaid = extractTransactionAmount(payment);
            rec.setAmount(amountPaid);
            String currency = payment != null && payment.get("currency_id") != null ? payment.get("currency_id").toString() : null;
            rec.setCurrency(currency);
            String method = payment != null && payment.get("payment_method_id") != null ? payment.get("payment_method_id").toString() : null;
            rec.setPaymentMethod(method);
            String description = payment != null && payment.get("description") != null ? payment.get("description").toString() : null;
            if (description == null || description.isBlank()) description = buildTitle(ref);
            rec.setTitle(description);
            OffsetDateTime created = extractApprovedAt(payment);
            if (created == null) created = OffsetDateTime.now();
            rec.setCreatedAt(created);
            paymentRecordPort.save(rec);
        } catch (Exception ignore) {}

        paymentService.subscribe(ref.userId, ref.type, ref.targetId);
        return ResponseEntity.noContent().build();
    }

    private DecodedRef decodeExternalReference(String externalRef) {
        if (externalRef == null) throw new IllegalArgumentException("null externalRef");
        String s = externalRef.trim();
        if (s.startsWith("<") && s.endsWith(">")) s = s.substring(1, s.length()-1);
        String[] parts = s.split(":");
        if (parts.length != 3) throw new IllegalArgumentException("bad format");
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
                return BigDecimal.valueOf(w.getPrice());
            }
            case CHAPTER -> {
                Chapter c = loadChapterPort.loadChapterForEdit(ref.targetId)
                        .orElseThrow(() -> new IllegalArgumentException("chapter not found"));
                if (c.getPrice() == null) return BigDecimal.ZERO;
                return BigDecimal.valueOf(c.getPrice());
            }
            case AUTHOR -> {
                if (authorPrice == null) return BigDecimal.ZERO;
                return authorPrice;
            }
            default -> { return BigDecimal.ZERO; }
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
                Chapter c = loadChapterPort.loadChapterForEdit(ref.targetId)
                        .orElse(null);
                if (c == null || c.getWorkId() == null) return null;
                return obtainWorkByIdPort.obtainWorkById(c.getWorkId())
                        .map(w -> w.getCreator() != null ? w.getCreator().getId() : null)
                        .orElse(null);
            }
            case AUTHOR -> {
                return ref.targetId;
            }
            default -> { return null; }
        }
    }

    private record DecodedRef(Long userId, SubscriptionType type, Long targetId) {}
}
