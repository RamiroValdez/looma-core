package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.ExtractPaymentIdFromWebhookUseCase;
import com.amool.application.usecases.ProcessMercadoPagoWebhookUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MercadoPagoWebhookControllerTest {

    private ExtractPaymentIdFromWebhookUseCase extractPaymentIdUseCase;
    private ProcessMercadoPagoWebhookUseCase processMercadoPagoWebhookUseCase;

    private MercadoPagoWebhookController controller;

    @BeforeEach
    void setup() {
        extractPaymentIdUseCase = mock(ExtractPaymentIdFromWebhookUseCase.class);
        processMercadoPagoWebhookUseCase = mock(ProcessMercadoPagoWebhookUseCase.class);

        controller = new MercadoPagoWebhookController(
                extractPaymentIdUseCase,
                processMercadoPagoWebhookUseCase
        );
    }

    private void givenPaymentIdExtractedFromParam(String idParam, String extractedId) {
        ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult extractResult =
                ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult.success(extractedId);
        when(extractPaymentIdUseCase.execute(eq(idParam), isNull())).thenReturn(extractResult);
    }

    private void givenPaymentIdExtractedFromBody(Map<String, Object> body, String extractedId) {
        ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult extractResult =
                ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult.success(extractedId);
        when(extractPaymentIdUseCase.execute(isNull(), eq(body))).thenReturn(extractResult);
    }

    private void givenPaymentIdExtractionError(String message) {
        ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult extractResult =
                ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult.error(message);
        when(extractPaymentIdUseCase.execute(isNull(), isNull())).thenReturn(extractResult);
    }

    private void givenProcessWebhookSucceeds(String paymentId) {
        ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult processResult =
                ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult.success();
        when(processMercadoPagoWebhookUseCase.execute(eq(paymentId), isNull(), isNull())).thenReturn(processResult);
    }

    private void givenProcessWebhookIgnored(String paymentId, String reason) {
        ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult processResult =
                ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult.ignored(reason);
        when(processMercadoPagoWebhookUseCase.execute(eq(paymentId), isNull(), isNull())).thenReturn(processResult);
    }

    private ResponseEntity<?> whenHandleGetPayment(String idParam) {
        return controller.handleGet("payment", "payment", idParam);
    }

    private ResponseEntity<?> whenHandlePostPayment(String idParam, Map<String, Object> body) {
        return controller.handlePost("payment", "payment", idParam, body);
    }

    private void thenResponseIs2xx(ResponseEntity<?> resp) {
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }

    private void thenResponseIs4xx(ResponseEntity<?> resp) {
        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }

    private void thenExtractCalledWithParam(String expectedId) {
        verify(extractPaymentIdUseCase).execute(eq(expectedId), isNull());
    }

    private void thenExtractCalledWithBody(Map<String, Object> expectedBody) {
        verify(extractPaymentIdUseCase).execute(isNull(), eq(expectedBody));
    }

    private void thenProcessCalledWith(String paymentId) {
        verify(processMercadoPagoWebhookUseCase).execute(eq(paymentId), isNull(), isNull());
    }

    private void thenNoProcessInteractions() {
        verifyNoInteractions(processMercadoPagoWebhookUseCase);
    }

    private Map<String, Object> bodyWithDataId(String id) {
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        body.put("data", data);
        return body;
    }

    @Test
    void approvedAuthorPayment_processesSuccessfully() {
        givenPaymentIdExtractedFromParam("p1", "p1");
        givenProcessWebhookSucceeds("p1");

        var resp = whenHandleGetPayment("p1");

        thenResponseIs2xx(resp);
        thenExtractCalledWithParam("p1");
        thenProcessCalledWith("p1");
    }

    @Test
    void webhookWithBody_extractsPaymentIdFromBody() {
        Map<String, Object> body = bodyWithDataId("p2");
        givenPaymentIdExtractedFromBody(body, "p2");
        givenProcessWebhookSucceeds("p2");

        var resp = whenHandlePostPayment(null, body);

        thenResponseIs2xx(resp);
        thenExtractCalledWithBody(body);
        thenProcessCalledWith("p2");
    }

    @Test
    void nonApprovedPayment_returnsNoContent() {
        givenPaymentIdExtractedFromParam("x", "x");
        givenProcessWebhookIgnored("x", "Payment not approved");

        var resp = whenHandleGetPayment("x");

        thenResponseIs2xx(resp);
        thenExtractCalledWithParam("x");
        thenProcessCalledWith("x");
    }

    @Test
    void missingPaymentId_returnsBadRequest() {
        givenPaymentIdExtractionError("Missing payment id");

        var resp = whenHandleGetPayment(null);

        thenResponseIs4xx(resp);
        thenExtractCalledWithParam(null);
        thenNoProcessInteractions();
    }
}
