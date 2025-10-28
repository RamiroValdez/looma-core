package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import com.amool.hexagonal.application.port.in.PaymentService;
import com.amool.hexagonal.application.port.out.PaymentAuditPort;
import com.amool.hexagonal.application.port.out.PaymentRecordPort;
import com.amool.hexagonal.application.port.out.UserBalancePort;
import com.amool.hexagonal.domain.model.SubscriptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MercadoPagoWebhookControllerTest {

    private PaymentService paymentService;
    private RestTemplate restTemplate;
    private PaymentAuditPort paymentAuditPort;
    private UserBalancePort userBalancePort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private LoadChapterPort loadChapterPort;
    private PaymentRecordPort paymentRecordPort;

    private MercadoPagoWebhookController controller;

    @BeforeEach
    void setup() {
        paymentService = mock(PaymentService.class);
        restTemplate = mock(RestTemplate.class);
        paymentAuditPort = mock(PaymentAuditPort.class);
        userBalancePort = mock(UserBalancePort.class);
        obtainWorkByIdPort = mock(ObtainWorkByIdPort.class);
        loadChapterPort = mock(LoadChapterPort.class);
        paymentRecordPort = mock(PaymentRecordPort.class);

        controller = new MercadoPagoWebhookController(
                paymentService, restTemplate, paymentAuditPort, userBalancePort,
                obtainWorkByIdPort, loadChapterPort, paymentRecordPort
        );
        ReflectionTestUtils.setField(controller, "accessToken", "token");
        ReflectionTestUtils.setField(controller, "apiBase", "https://api.mercadopago.com");
        ReflectionTestUtils.setField(controller, "authorPrice", new BigDecimal("1500"));
    }

    private Map<String, Object> approvedPayment(String paymentId, String externalRef, BigDecimal amount) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", paymentId);
        map.put("status", "approved");
        map.put("external_reference", externalRef);
        map.put("currency_id", "ARS");
        map.put("payment_method_id", "account_money");
        map.put("transaction_amount", amount);
        map.put("date_approved", OffsetDateTime.now().toString());
        map.put("description", "Suscripción test");
        return map;
    }

    @Test
    void approvedAuthorPayment_credits92Percent_andCreatesSubscription() {
        when(paymentAuditPort.markProcessedIfFirst("p1")).thenReturn(true);

        String ref = "10:" + SubscriptionType.AUTHOR.name().toLowerCase() + ":2";
        Map<String, Object> payment = approvedPayment("p1", ref, new BigDecimal("1000"));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(payment));

        var resp = controller.handleGet("payment", "payment", "p1");
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();

        verify(userBalancePort).addMoney(eq(2L), argThat(bd -> bd != null && bd.compareTo(new BigDecimal("920.0")) == 0));
        verify(paymentService).subscribe(10L, SubscriptionType.AUTHOR, 2L);
        verify(paymentRecordPort).save(any());
    }

    @Test
    void idempotency_onlyFirstWebhookCredits() {
        when(paymentAuditPort.markProcessedIfFirst("p2")).thenReturn(true).thenReturn(false);
        String ref = "11:" + SubscriptionType.WORK.name().toLowerCase() + ":5";
        Work w = new Work();
        User creator = new User(); creator.setId(77L); w.setCreator(creator); w.setPrice(2000.0); w.setId(5L);
        when(obtainWorkByIdPort.obtainWorkById(5L)).thenReturn(Optional.of(w));
        Map<String, Object> payment = approvedPayment("p2", ref, new BigDecimal("2000"));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(payment));

        var resp1 = controller.handleGet("payment", "payment", "p2");
        var resp2 = controller.handleGet("payment", "payment", "p2");
        assertThat(resp1.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp2.getStatusCode().is2xxSuccessful()).isTrue();

        verify(userBalancePort, times(1)).addMoney(eq(77L), any());
        verify(paymentService, times(2)).subscribe(11L, SubscriptionType.WORK, 5L);
    }

    @Test
    void nonApprovedIgnored() {
        Map<String, Object> payment = new HashMap<>();
        payment.put("status", "pending");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(payment));

        var resp = controller.handleGet("payment", "payment", "x");
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        verifyNoInteractions(paymentService);
        verifyNoInteractions(userBalancePort);
    }
}
