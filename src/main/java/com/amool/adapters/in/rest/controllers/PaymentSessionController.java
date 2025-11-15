package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.LinkPaymentSessionRequest;
import com.amool.application.port.out.PaymentRecordPort;
import com.amool.domain.model.PaymentRecord;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/payments/session")
public class PaymentSessionController {

    private final PaymentRecordPort paymentRecordPort;

    public PaymentSessionController(PaymentRecordPort paymentRecordPort) {
        this.paymentRecordPort = paymentRecordPort;
    }
    
    @GetMapping("/{uuid}")
    public ResponseEntity<?> get(@PathVariable("uuid") String uuid) {
        var opt = paymentRecordPort.findBySessionUuid(uuid);
        if (opt.isPresent()) {
            return toResponse(opt.get());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("status", "PENDING");
        body.put("sessionUuid", uuid);
        return ResponseEntity.ok(body);
    }

    private ResponseEntity<?> toResponse(PaymentRecord r) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", r.getId());
        body.put("status", r.getStatus());
        body.put("provider", r.getProvider());
        body.put("userId", r.getUserId());
        body.put("subscriptionType", r.getSubscriptionType());
        body.put("targetId", r.getTargetId());
        body.put("amount", r.getAmount());
        body.put("currency", r.getCurrency());
        body.put("paymentMethod", r.getPaymentMethod());
        body.put("title", r.getTitle());
        body.put("createdAt", r.getCreatedAt());
        body.put("externalReference", r.getExternalReference());
        body.put("sessionUuid", r.getSessionUuid());
        return ResponseEntity.ok(body);
    }
}
