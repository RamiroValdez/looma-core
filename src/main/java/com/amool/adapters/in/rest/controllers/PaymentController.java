package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.SubscribeRequest;
import com.amool.adapters.in.rest.dtos.PaymentInitResponse;
import com.amool.application.service.PaymentService;
import com.amool.domain.model.PaymentInitResult;
import com.amool.domain.model.PaymentProviderType;
import com.amool.domain.model.SubscriptionType;
import com.amool.security.JwtUserPrincipal;
import com.amool.application.usecases.SubscribeUserUseCase;
import com.amool.application.port.out.ObtainWorkByIdPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.amool.application.port.out.LoadChapterPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import com.amool.domain.model.Work;
import com.amool.domain.model.Chapter;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final LoadChapterPort loadChapterPort;
    private final SubscribeUserUseCase subscribeUserUseCase;
    private final ObtainWorkByIdPort obtainWorkByIdPort;

    @Value("${payments.pricing.author:0}")
    private BigDecimal authorPrice;

    public PaymentController(PaymentService paymentService,
                           LoadChapterPort loadChapterPort,
                           SubscribeUserUseCase subscribeUserUseCase,
                           ObtainWorkByIdPort obtainWorkByIdPort) {
        this.paymentService = paymentService;
        this.loadChapterPort = loadChapterPort;
        this.subscribeUserUseCase = subscribeUserUseCase;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
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

        BigDecimal price = null;
        if (type == SubscriptionType.AUTHOR) {
            if (authorPrice == null) {
                return ResponseEntity.badRequest().body("Author subscription disabled");
            }
            price = authorPrice;
        } else if (type == SubscriptionType.WORK) {
            var workOpt = obtainWorkByIdPort.obtainWorkById(request.targetId());
            if (workOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Work not found");
            }
            Work work = workOpt.get();
            price = work.getPrice() == null ? BigDecimal.ZERO : work.getPrice();
        } else if (type == SubscriptionType.CHAPTER) {
            if (request.workId() == null) {
                return ResponseEntity.badRequest().body("workId is required for chapter subscription");
            }
            var chapterOpt = loadChapterPort.loadChapter(request.workId(), request.targetId());
            if (chapterOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Chapter does not belong to the specified work");
            }
            Chapter chapter = chapterOpt.get();
            price = chapter.getPrice() == null ? BigDecimal.ZERO : chapter.getPrice();
        }

        if (price != null && price.compareTo(BigDecimal.ZERO) <= 0) {
            subscribeUserUseCase.execute(userId, type, request.targetId());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        if (request.provider() == null || request.provider().isBlank()) {
            return ResponseEntity.badRequest().body("Provider required");
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
