package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.controllers.MercadoPagoWebhookController;
import com.amool.application.usecases.ExtractPaymentIdFromWebhookUseCase;
import com.amool.application.usecases.ProcessMercadoPagoWebhookUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void approvedAuthorPayment_processesSuccessfully() {
        // Arrange
        ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult extractResult =
            ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult.success("p1");
        when(extractPaymentIdUseCase.execute(eq("p1"), isNull()))
            .thenReturn(extractResult);

        ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult processResult =
            ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult.success();
        when(processMercadoPagoWebhookUseCase.execute(eq("p1"), isNull(), isNull()))
            .thenReturn(processResult);

        // Act
        var resp = controller.handleGet("payment", "payment", "p1");

        // Assert
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        verify(extractPaymentIdUseCase).execute(eq("p1"), isNull());
        verify(processMercadoPagoWebhookUseCase).execute(eq("p1"), isNull(), isNull());
    }

    @Test
    void webhookWithBody_extractsPaymentIdFromBody() {
        // Arrange
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", "p2");
        body.put("data", data);

        ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult extractResult =
            ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult.success("p2");
        when(extractPaymentIdUseCase.execute(isNull(), eq(body)))
            .thenReturn(extractResult);

        ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult processResult =
            ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult.success();
        when(processMercadoPagoWebhookUseCase.execute(eq("p2"), isNull(), isNull()))
            .thenReturn(processResult);

        // Act
        var resp = controller.handlePost("payment", "payment", null, body);

        // Assert
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        verify(extractPaymentIdUseCase).execute(isNull(), eq(body));
        verify(processMercadoPagoWebhookUseCase).execute(eq("p2"), isNull(), isNull());
    }

    @Test
    void nonApprovedPayment_returnsNoContent() {
        // Arrange
        ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult extractResult =
            ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult.success("x");
        when(extractPaymentIdUseCase.execute(eq("x"), isNull()))
            .thenReturn(extractResult);

        ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult processResult =
            ProcessMercadoPagoWebhookUseCase.ProcessMercadoPagoWebhookResult.ignored("Payment not approved");
        when(processMercadoPagoWebhookUseCase.execute(eq("x"), isNull(), isNull()))
            .thenReturn(processResult);

        // Act
        var resp = controller.handleGet("payment", "payment", "x");

        // Assert
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        verify(extractPaymentIdUseCase).execute(eq("x"), isNull());
        verify(processMercadoPagoWebhookUseCase).execute(eq("x"), isNull(), isNull());
    }

    @Test
    void missingPaymentId_returnsBadRequest() {
        // Arrange
        ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult extractResult =
            ExtractPaymentIdFromWebhookUseCase.ExtractPaymentIdResult.error("Missing payment id");
        when(extractPaymentIdUseCase.execute(isNull(), isNull()))
            .thenReturn(extractResult);

        // Act
        var resp = controller.handleGet("payment", "payment", null);

        // Assert
        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
        verify(extractPaymentIdUseCase).execute(isNull(), isNull());
        verifyNoInteractions(processMercadoPagoWebhookUseCase);
    }
}
