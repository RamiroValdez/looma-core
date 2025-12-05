package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.ExtractPaymentIdFromWebhook;
import com.amool.application.usecases.ProcessMercadoPagoWebhook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/webhook/mercadopago")
public class MercadoPagoWebhookController {

    private final ExtractPaymentIdFromWebhook extractPaymentIdUseCase;
    private final ProcessMercadoPagoWebhook processMercadoPagoWebhook;

    public MercadoPagoWebhookController(ExtractPaymentIdFromWebhook extractPaymentIdUseCase,
                                        ProcessMercadoPagoWebhook processMercadoPagoWebhook) {
        this.extractPaymentIdUseCase = extractPaymentIdUseCase;
        this.processMercadoPagoWebhook = processMercadoPagoWebhook;
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
            ExtractPaymentIdFromWebhook.ExtractPaymentIdResult extractResult =
                extractPaymentIdUseCase.execute(id, body);

            if (!extractResult.isSuccess()) {
                return ResponseEntity.badRequest().body(extractResult.getErrorMessage());
            }

            ProcessMercadoPagoWebhook.ProcessMercadoPagoWebhookResult result =
                processMercadoPagoWebhook.execute(
                    extractResult.getPaymentId(),
                    extractResult.getExternalReference(),
                    null
                );

            if (result.isIgnored()) {
                return ResponseEntity.noContent().build();
            }

            if (!result.isSuccess()) {
                return ResponseEntity.status(500).body(result.getErrorMessage());
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Webhook processing error");
        }
    }
}
