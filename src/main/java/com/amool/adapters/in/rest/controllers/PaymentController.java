package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.SubscribeRequest;
import com.amool.adapters.in.rest.dtos.PaymentInitResponse;
import com.amool.application.usecases.StartSubscriptionFlowUseCase;
import com.amool.domain.model.PaymentInitResult;
import com.amool.security.JwtUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final StartSubscriptionFlowUseCase startSubscriptionFlowUseCase;

    public PaymentController(StartSubscriptionFlowUseCase startSubscriptionFlowUseCase) {
        this.startSubscriptionFlowUseCase = startSubscriptionFlowUseCase;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody SubscribeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = principal.getUserId();

        StartSubscriptionFlowUseCase.Result result;
        try {
            result = startSubscriptionFlowUseCase.execute(
                    userId,
                    request.subscriptionType(),
                    request.targetId(),
                    request.workId(),
                    request.provider(),
                    request.returnUrl()
            );
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if ("Invalid subscriptionType".equals(msg) ||
                "Cannot subscribe to yourself".equals(msg) ||
                "Author subscription disabled".equals(msg) ||
                "Work not found".equals(msg) ||
                "workId is required for chapter subscription".equals(msg) ||
                "Chapter does not belong to the specified work".equals(msg) ||
                "Provider required".equals(msg) ||
                "Invalid provider".equals(msg)) {
                return ResponseEntity.badRequest().body(msg);
            }
            if (msg != null && msg.startsWith("Payment provider not configured")) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(msg);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (result.isFree()) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        PaymentInitResult init = result.getPaymentInit();
        return ResponseEntity.ok(PaymentInitResponse.from(init));
    }
}
