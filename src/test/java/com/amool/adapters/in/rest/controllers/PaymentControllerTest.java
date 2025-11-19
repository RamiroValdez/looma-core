package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.PaymentInitResponse;
import com.amool.adapters.in.rest.dtos.SubscribeRequest;
import com.amool.application.usecases.StartSubscriptionFlowUseCase;
import com.amool.domain.model.PaymentInitResult;
import com.amool.domain.model.PaymentProviderType;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PaymentControllerTest {

    private PaymentController controller;
    private StartSubscriptionFlowUseCase startSubscriptionFlowUseCase;

    private static final Long USER_ID = 777L;

    @BeforeEach
    void setUp() {
        startSubscriptionFlowUseCase = Mockito.mock(StartSubscriptionFlowUseCase.class);
        controller = new PaymentController(startSubscriptionFlowUseCase);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /api/payments/subscribe - 401 cuando no hay autenticación")
    void subscribe_shouldReturn401_whenNoAuth() {
        SecurityContextHolder.clearContext();
        SubscribeRequest request = anyValidRequest();

        ResponseEntity<?> response = controller.subscribe(request);

        assertStatus(response, HttpStatus.UNAUTHORIZED);
        verify(startSubscriptionFlowUseCase, never()).execute(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/payments/subscribe - 401 cuando el principal no es JwtUserPrincipal")
    void subscribe_shouldReturn401_whenPrincipalNotJwt() {
        setAuthentication(new UsernamePasswordAuthenticationToken("someone", null));
        SubscribeRequest request = anyValidRequest();

        ResponseEntity<?> response = controller.subscribe(request);

        assertStatus(response, HttpStatus.UNAUTHORIZED);
        verify(startSubscriptionFlowUseCase, never()).execute(any(), any(), any(), any(), any(), any());
    }


    @Test
    @DisplayName("POST /api/payments/subscribe - 201 cuando la suscripción es gratuita")
    void subscribe_shouldReturn201_whenFree() {
        setAuthenticatedUser();
        SubscribeRequest request = new SubscribeRequest("work", 10L, null, null, "https://return");
        when(startSubscriptionFlowUseCase.execute(eq(USER_ID), eq("work"), eq(10L), isNull(), isNull(), eq("https://return")))
                .thenReturn(StartSubscriptionFlowUseCase.Result.free());

        ResponseEntity<?> response = controller.subscribe(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("POST /api/payments/subscribe - 200 y body cuando requiere pago")
    void subscribe_shouldReturn200_withPaymentInit_whenPaymentRequired() {
        setAuthenticatedUser();
        SubscribeRequest request = new SubscribeRequest("author", 55L, null, "mercadopago", "https://return-url");
        PaymentInitResult init = PaymentInitResult.of(PaymentProviderType.MERCADOPAGO, "https://pay.example/abc", "pref-123");
        when(startSubscriptionFlowUseCase.execute(eq(USER_ID), eq("author"), eq(55L), isNull(), eq("mercadopago"), eq("https://return-url")))
                .thenReturn(StartSubscriptionFlowUseCase.Result.payment(init));

        ResponseEntity<?> response = controller.subscribe(request);

        assertStatus(response, HttpStatus.OK);
        assertNotNull(response.getBody());
        PaymentInitResponse body = assertInstanceOf(PaymentInitResponse.class, response.getBody());
        assertEquals("mercadopago", body.provider());
        assertEquals("https://pay.example/abc", body.redirectUrl());
        assertEquals("pref-123", body.externalReference());
    }

    @Test
    @DisplayName("POST /api/payments/subscribe - llama al use case con los parámetros correctos")
    void subscribe_shouldCallUseCase_withCorrectArgs() {
        setAuthenticatedUser();
        SubscribeRequest request = new SubscribeRequest("chapter", 77L, 999L, "mp", "http://back");
        when(startSubscriptionFlowUseCase.execute(anyLong(), anyString(), anyLong(), any(), anyString(), anyString()))
                .thenReturn(StartSubscriptionFlowUseCase.Result.free());

        controller.subscribe(request);

        ArgumentCaptor<Long> userId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> type = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> target = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> workId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> provider = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> returnUrl = ArgumentCaptor.forClass(String.class);

        verify(startSubscriptionFlowUseCase).execute(
                userId.capture(), type.capture(), target.capture(), workId.capture(), provider.capture(), returnUrl.capture()
        );

        assertEquals(USER_ID, userId.getValue());
        assertEquals("chapter", type.getValue());
        assertEquals(77L, target.getValue());
        assertEquals(999L, workId.getValue());
        assertEquals("mp", provider.getValue());
        assertEquals("http://back", returnUrl.getValue());
    }


    @Test
    @DisplayName("subscribe - 400 Invalid subscriptionType")
    void subscribe_shouldReturn400_invalidSubscriptionType() {
        assertMapsToBadRequest("Invalid subscriptionType");
    }

    @Test
    @DisplayName("subscribe - 400 Cannot subscribe to yourself")
    void subscribe_shouldReturn400_cannotSubscribeToYourself() {
        assertMapsToBadRequest("Cannot subscribe to yourself");
    }

    @Test
    @DisplayName("subscribe - 400 Author subscription disabled")
    void subscribe_shouldReturn400_authorSubscriptionDisabled() {
        assertMapsToBadRequest("Author subscription disabled");
    }

    @Test
    @DisplayName("subscribe - 400 Work not found")
    void subscribe_shouldReturn400_workNotFound() {
        assertMapsToBadRequest("Work not found");
    }

    @Test
    @DisplayName("subscribe - 400 workId is required for chapter subscription")
    void subscribe_shouldReturn400_workIdRequiredForChapter() {
        assertMapsToBadRequest("workId is required for chapter subscription");
    }

    @Test
    @DisplayName("subscribe - 400 Chapter does not belong to the specified work")
    void subscribe_shouldReturn400_chapterNotBelongToWork() {
        assertMapsToBadRequest("Chapter does not belong to the specified work");
    }

    @Test
    @DisplayName("subscribe - 400 Provider required")
    void subscribe_shouldReturn400_providerRequired() {
        assertMapsToBadRequest("Provider required");
    }

    @Test
    @DisplayName("subscribe - 400 Invalid provider")
    void subscribe_shouldReturn400_invalidProvider() {
        assertMapsToBadRequest("Invalid provider");
    }


    @Test
    @DisplayName("subscribe - 412 Payment provider not configured")
    void subscribe_shouldReturn412_providerNotConfigured() {
        setAuthenticatedUser();
        SubscribeRequest request = anyValidRequest();
        when(startSubscriptionFlowUseCase.execute(anyLong(), anyString(), anyLong(), any(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Payment provider not configured: MERCADOPAGO"));

        ResponseEntity<?> response = controller.subscribe(request);

        assertStatus(response, HttpStatus.PRECONDITION_FAILED);
        assertEquals("Payment provider not configured: MERCADOPAGO", response.getBody());
    }


    @Test
    @DisplayName("subscribe - 500 en IllegalArgumentException desconocido")
    void subscribe_shouldReturn500_onUnknownIllegalArgument() {
        setAuthenticatedUser();
        SubscribeRequest request = anyValidRequest();
        when(startSubscriptionFlowUseCase.execute(anyLong(), anyString(), anyLong(), any(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Some other error"));

        ResponseEntity<?> response = controller.subscribe(request);

        assertStatus(response, HttpStatus.INTERNAL_SERVER_ERROR);
        assertNull(response.getBody());
    }

        
    private SubscribeRequest anyValidRequest() {
        return new SubscribeRequest("work", 1L, null, "mp", "http://return");
    }

    private void setAuthenticatedUser() {
        JwtUserPrincipal principal = new JwtUserPrincipal(
                USER_ID,
                "user@example.com",
                "John",
                "Doe",
                "johndoe"
        );
        setAuthentication(new UsernamePasswordAuthenticationToken(principal, null));
    }

    private void setAuthentication(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void assertStatus(ResponseEntity<?> response, HttpStatus status) {
        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
    }

    private void assertMapsToBadRequest(String message) {
        setAuthenticatedUser();
        SubscribeRequest request = anyValidRequest();
        when(startSubscriptionFlowUseCase.execute(anyLong(), anyString(), anyLong(), any(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException(message));

        ResponseEntity<?> response = controller.subscribe(request);

        assertStatus(response, HttpStatus.BAD_REQUEST);
        assertEquals(message, response.getBody());
    }
}
