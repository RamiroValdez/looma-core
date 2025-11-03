package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.ExtractPaymentIdFromWebhookUseCase;
import com.amool.application.usecases.ProcessMercadoPagoWebhookUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/webhook/mercadopago")
public class MercadoPagoWebhookController {

    private final ExtractPaymentIdFromWebhookUseCase extractPaymentIdUseCase;
    private final ProcessMercadoPagoWebhookUseCase processMercadoPagoWebhookUseCase;

    public MercadoPagoWebhookController(ExtractPaymentIdFromWebhookUseCase extractPaymentIdUseCase,
                                       ProcessMercadoPagoWebhookUseCase processMercadoPagoWebhookUseCase) {
        this.extractPaymentIdUseCase = extractPaymentIdUseCase;
        this.processMercadoPagoWebhookUseCase = processMercadoPagoWebhookUseCase;
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
            // Extraer el payment ID del webhook
            ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult extractResult =
                extractPaymentIdUseCase.execute(id, body);

            if (!extractResult.isSuccess()) {
                return ResponseEntity.badRequest().body(extractResult.getErrorMessage());
            }

            // Procesar el webhook de MercadoPago
            ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult result =
                processMercadoPagoWebhookUseCase.execute(
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
