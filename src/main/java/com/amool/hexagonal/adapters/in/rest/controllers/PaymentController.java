package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.SubscribeRequest;
import com.amool.hexagonal.adapters.in.rest.dtos.PaymentInitResponse;
import com.amool.hexagonal.application.port.in.PaymentService;
import com.amool.hexagonal.domain.model.PaymentInitResult;
import com.amool.hexagonal.domain.model.PaymentProviderType;
import com.amool.hexagonal.domain.model.SubscriptionType;
import com.amool.security.JwtUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.amool.application.port.out.LoadChapterPort;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final LoadChapterPort loadChapterPort;

    @Value("${payments.pricing.author:0}")
    private BigDecimal authorPrice;

    public PaymentController(PaymentService paymentService, LoadChapterPort loadChapterPort) {
        this.paymentService = paymentService;
        this.loadChapterPort = loadChapterPort;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody SubscribeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = principal.getUserId();

        SubscriptionType type;
        try {
            type = SubscriptionType.fromString(request.subscriptionType());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid subscriptionType");
        }

        if (type == SubscriptionType.AUTHOR && request.targetId().equals(userId)) {
            return ResponseEntity.badRequest().body("Cannot subscribe to yourself");
        }

        if (type == SubscriptionType.CHAPTER && request.workId() != null) {
            var chapterOpt = loadChapterPort.loadChapter(request.workId(), request.targetId());
            if (chapterOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Chapter does not belong to the specified work");
            }
        }

        if (request.provider() == null || request.provider().isBlank()) {
            if (type == SubscriptionType.AUTHOR) {
                if (authorPrice == null || authorPrice.compareTo(BigDecimal.ONE) < 0) {
                    return ResponseEntity.badRequest().body("Author subscription disabled");
                }
                if (request.targetId().equals(userId)) {
                    return ResponseEntity.badRequest().body("Cannot subscribe to yourself");
                }
            }
            paymentService.subscribe(userId, type, request.targetId());
            return ResponseEntity.noContent().build();
        }

        PaymentProviderType provider;
        try {
            provider = PaymentProviderType.fromString(request.provider());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid provider");
        }

        PaymentInitResult result = paymentService.startCheckout(userId, type, request.targetId(), provider, request.returnUrl());
        return ResponseEntity.ok(PaymentInitResponse.from(result));
    }
}
