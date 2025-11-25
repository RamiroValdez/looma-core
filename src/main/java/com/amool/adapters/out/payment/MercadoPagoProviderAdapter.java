package com.amool.adapters.out.payment;

import com.amool.application.port.out.PaymentProviderPort;
import com.amool.domain.model.PaymentInitResult;
import com.amool.domain.model.PaymentProviderType;
import com.amool.domain.model.SubscriptionType;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.UserQueryPort;
import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import com.amool.application.port.out.PaymentSessionLinkPort;

@Component
public class MercadoPagoProviderAdapter implements PaymentProviderPort {

    @Value("${payments.mercadopago.accessToken}")
    private String accessToken;

    @Value("${payments.webhook.mercadopago}")
    private String notificationUrl;

    @Value("${payments.successUrl}")
    private String successUrl;

    @Value("${payments.cancelUrl}")
    private String cancelUrl;

    @Value("${payments.mercadopago.apiBase:https://api.mercadopago.com}")
    private String apiBase;

    @Value("${payments.currency:ARS}")
    private String currency;

    @Value("${payments.pricing.chapter:1.0}")
    private BigDecimal chapterPrice;

    // Eliminamos el uso del property de autor para evitar defaults globales
    // @Value("${payments.pricing.author:1.0}")
    // private BigDecimal authorPrice;

    @Value("${payments.pricing.work.fallback:1.0}")
    private BigDecimal workFallbackPrice;

    private final RestTemplate restTemplate;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final LoadChapterPort loadChapterPort;
    private final UserQueryPort userQueryPort;
    private final LoadUserPort loadUserPort;
    private final PaymentSessionLinkPort paymentSessionLinkPort;

    public MercadoPagoProviderAdapter(RestTemplate restTemplate,
                                      ObtainWorkByIdPort obtainWorkByIdPort,
                                      LoadChapterPort loadChapterPort,
                                      UserQueryPort userQueryPort,
                                      LoadUserPort loadUserPort,
                                      PaymentSessionLinkPort paymentSessionLinkPort) {
        this.restTemplate = restTemplate;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadChapterPort = loadChapterPort;
        this.userQueryPort = userQueryPort;
        this.loadUserPort = loadUserPort;
        this.paymentSessionLinkPort = paymentSessionLinkPort;
    }

    @Override
    public PaymentProviderType supportedProvider() {
        return PaymentProviderType.MERCADOPAGO;
    }

    @Override
    public PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId) {
        return startCheckout(userId, type, targetId, null);
    }

    public PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId, String returnUrl) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("MercadoPago access token not configured (payments.mercadopago.accessToken)");
        }

        String sessionUuid = null;
        try {
            if (returnUrl != null && !returnUrl.isBlank()) {
                URI u = URI.create(returnUrl);
                String path = u.getPath();
                if (path != null) {
                    int idx = path.indexOf("/payment/");
                    if (idx >= 0) {
                        String tail = path.substring(idx + "/payment/".length());
                        int nextSlash = tail.indexOf('/');
                        if (nextSlash > 0) tail = tail.substring(0, nextSlash);
                        String candidate = tail;
                        if (candidate.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
                            sessionUuid = candidate;
                        }
                    }
                }
            }
        } catch (Exception ignore) {}
        String externalRef = encodeRef(userId, type, targetId);
        if (sessionUuid != null) {
            paymentSessionLinkPort.saveLink(externalRef, sessionUuid);
        }
        System.out.println("sessionUuid sessionUuid: " + sessionUuid);
        System.out.println("returnUrl returnUrl: " + returnUrl);
        BigDecimal amount;
        String title;
        switch (type) {
            case WORK -> {
                Work work = obtainWorkByIdPort.obtainWorkById(targetId)
                        .orElseThrow(() -> new IllegalArgumentException("Work not found"));
                if (work.getCreator() != null && work.getCreator().getId() != null && work.getCreator().getId().equals(userId)) {
                    throw new IllegalArgumentException("Cannot subscribe to own work");
                }
                if (work.getPrice() == null ||  work.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Invalid work price");
                }
                amount = work.getPrice();
                title = "Suscripción a " + (work.getTitle() == null ? ("work-" + work.getId()) : work.getTitle());
            }
            case CHAPTER -> {
                Chapter chapter = loadChapterPort.loadChapterForEdit(targetId)
                        .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
                if (chapter.getWorkId() == null) {
                    throw new IllegalArgumentException("Chapter without work");
                }
                Work work = obtainWorkByIdPort.obtainWorkById(chapter.getWorkId())
                        .orElseThrow(() -> new IllegalArgumentException("Work for chapter not found"));
                if (work.getCreator() != null && work.getCreator().getId() != null && work.getCreator().getId().equals(userId)) {
                    throw new IllegalArgumentException("Cannot subscribe to own chapter");
                }
                if (chapter.getPrice() == null || chapter.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Invalid chapter price");
                }
                amount = chapter.getPrice();
                String wTitle = work.getTitle() == null ? ("work-" + work.getId()) : work.getTitle();
                String cTitle = chapter.getTitle() == null ? ("cap-" + chapter.getId()) : chapter.getTitle();
                title = "Suscripción a " + wTitle + " - " + cTitle;
            }
            case AUTHOR -> {
                if (targetId.equals(userId)) {
                    throw new IllegalArgumentException("Cannot subscribe to yourself");
                }
                if (!userQueryPort.existsById(targetId)) {
                    throw new IllegalArgumentException("Author not found");
                }
                // Tomamos el precio real del autor sin defaults
                User author = loadUserPort.getById(targetId)
                        .orElseThrow(() -> new IllegalArgumentException("Author not found"));
                if (author.getPrice() == null || author.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Invalid author price");
                }
                amount = author.getPrice();
                String authorName = userQueryPort.findNameById(targetId);
                title = "Suscripción a autor" + (authorName == null || authorName.isBlank() ? (" " + targetId) : (" " + authorName));
            }
            default -> throw new IllegalArgumentException("Unsupported type");
        }

        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("quantity", 1);
        item.put("currency_id", currency);
        item.put("unit_price", amount);

        Map<String, Object> backUrls = new HashMap<>();
        if (returnUrl != null && !returnUrl.isBlank()) {
            String base = returnUrl;
            String sep = (base.contains("?")) ? "&" : "?";
            backUrls.put("success", base + sep + "result=success&ref=" + externalRef + "&provider=mercadopago");
            backUrls.put("pending", base + sep + "result=pending&ref=" + externalRef + "&provider=mercadopago");
            backUrls.put("failure", base + sep + "result=failure&ref=" + externalRef + "&provider=mercadopago");
        } else {
            backUrls.put("success", successUrl);
            backUrls.put("pending", successUrl);
            backUrls.put("failure", cancelUrl);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", new ArrayList<>(List.of(item)));
        payload.put("external_reference", externalRef);
        if (sessionUuid != null) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("session_uuid", sessionUuid);
            payload.put("metadata", metadata);
        }
        payload.put("back_urls", backUrls);
        payload.put("notification_url", notificationUrl);
        String autoReturnTarget = (returnUrl != null && !returnUrl.isBlank()) ? returnUrl : successUrl;
        if (isHttpsPublicUrl(autoReturnTarget)) {
            payload.put("auto_return", "approved");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        String url = apiBase + "/checkout/preferences";
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("Empty response from MercadoPago when creating preference");
        }
        Object initPoint = body.get("init_point");
        Object prefId = body.get("id");
        return PaymentInitResult.of(PaymentProviderType.MERCADOPAGO, String.valueOf(initPoint), String.valueOf(prefId));
    }

    private String encodeRef(Long userId, SubscriptionType type, Long targetId) {
        return userId + ":" + type.name().toLowerCase() + ":" + targetId;
    }


    private boolean isHttpsPublicUrl(String url) {
        if (url == null || url.isBlank()) return false;
        String u = url.toLowerCase();
        if (!u.startsWith("https://")) return false;
        return !(u.contains("localhost") || u.contains("127.0.0.1"));
    }
}
