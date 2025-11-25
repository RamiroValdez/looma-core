package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.PaymentInitResponse;
import com.amool.adapters.in.rest.dtos.SubscribeRequest;
import com.amool.application.usecases.StartSubscriptionFlow;
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
    private StartSubscriptionFlow startSubscriptionFlow;

    private static final Long USER_ID = 777L;

    @BeforeEach
    void setUp() {
        startSubscriptionFlow = Mockito.mock(StartSubscriptionFlow.class);
        controller = new PaymentController(startSubscriptionFlow);
        SecurityContextHolder.clearContext();
    }

    private void givenNoAuthentication() {
        SecurityContextHolder.clearContext();
    }

    private void givenPrincipalNotJwt() {
        setAuthentication(new UsernamePasswordAuthenticationToken("someone", null));
    }

    private void givenAuthenticatedUser() {
        JwtUserPrincipal principal = new JwtUserPrincipal(
                USER_ID,
                "user@example.com",
                "John",
                "Doe",
                "johndoe"
        );
        setAuthentication(new UsernamePasswordAuthenticationToken(principal, null));
    }

    private void givenUseCaseReturnsFree(SubscribeRequest request) {
        when(startSubscriptionFlow.execute(eq(USER_ID), eq(request.subscriptionType()), eq(request.targetId()),
                eq(request.workId()), eq(request.provider()), eq(request.returnUrl())))
                .thenReturn(StartSubscriptionFlow.Result.free());
    }

    private void givenUseCaseReturnsPayment(SubscribeRequest request, PaymentInitResult init) {
        when(startSubscriptionFlow.execute(eq(USER_ID), eq(request.subscriptionType()), eq(request.targetId()),
                eq(request.workId()), eq(request.provider()), eq(request.returnUrl())))
                .thenReturn(StartSubscriptionFlow.Result.payment(init));
    }

    private void givenUseCaseThrowsIllegalArgument(String message) {
        when(startSubscriptionFlow.execute(anyLong(), anyString(), anyLong(), any(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException(message));
    }

    private void givenUseCaseReturnsFreeForAnyArgs() {
        when(startSubscriptionFlow.execute(anyLong(), anyString(), anyLong(), any(), anyString(), anyString()))
                .thenReturn(StartSubscriptionFlow.Result.free());
    }

    // ------------------- when -------------------
    private ResponseEntity<?> whenSubscribe(SubscribeRequest request) {
        return controller.subscribe(request);
    }

    // ------------------- then -------------------
    private void thenStatusIs(ResponseEntity<?> response, HttpStatus status) {
        assertNotNull(response);
        assertEquals(status, response.getStatusCode());
    }

    private void thenBodyIsNull(ResponseEntity<?> response) {
        assertNull(response.getBody());
    }

    private void thenBodyEquals(ResponseEntity<?> response, Object expected) {
        assertEquals(expected, response.getBody());
    }

    private void thenBodyIsPaymentInit(ResponseEntity<?> response, String provider, String redirectUrl, String externalReference) {
        assertNotNull(response.getBody());
        PaymentInitResponse body = assertInstanceOf(PaymentInitResponse.class, response.getBody());
        assertEquals(provider, body.provider());
        assertEquals(redirectUrl, body.redirectUrl());
        assertEquals(externalReference, body.externalReference());
    }

    private void thenUseCaseNotCalled() {
        verify(startSubscriptionFlow, never()).execute(any(), any(), any(), any(), any(), any());
    }

    private void thenUseCaseCalledWithArgs(SubscribeRequest request) {
        ArgumentCaptor<Long> userId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> type = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> target = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> workId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> provider = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> returnUrl = ArgumentCaptor.forClass(String.class);

        verify(startSubscriptionFlow).execute(
                userId.capture(), type.capture(), target.capture(), workId.capture(), provider.capture(), returnUrl.capture()
        );

        assertEquals(USER_ID, userId.getValue());
        assertEquals(request.subscriptionType(), type.getValue());
        assertEquals(request.targetId(), target.getValue());
        assertEquals(request.workId(), workId.getValue());
        assertEquals(request.provider(), provider.getValue());
        assertEquals(request.returnUrl(), returnUrl.getValue());
    }

    private SubscribeRequest request(String subscriptionType, Long targetId, Long workId, String provider, String returnUrl) {
        return new SubscribeRequest(subscriptionType, targetId, workId, provider, returnUrl);
    }

    private SubscribeRequest anyValidRequest() {
        return request("work", 1L, null, "mp", "http://return");
    }

    private void setAuthentication(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("POST /api/payments/subscribe - 401 cuando no hay autenticación")
    void subscribe_shouldReturn401_whenNoAuth() {
        givenNoAuthentication();
        SubscribeRequest request = anyValidRequest();

        ResponseEntity<?> response = whenSubscribe(request);

        thenStatusIs(response, HttpStatus.UNAUTHORIZED);
        thenUseCaseNotCalled();
    }

    @Test
    @DisplayName("POST /api/payments/subscribe - 401 cuando el principal no es JwtUserPrincipal")
    void subscribe_shouldReturn401_whenPrincipalNotJwt() {
        givenPrincipalNotJwt();
        SubscribeRequest request = anyValidRequest();

        ResponseEntity<?> response = whenSubscribe(request);

        thenStatusIs(response, HttpStatus.UNAUTHORIZED);
        thenUseCaseNotCalled();
    }

    @Test
    @DisplayName("POST /api/payments/subscribe - 201 cuando la suscripción es gratuita")
    void subscribe_shouldReturn201_whenFree() {
        givenAuthenticatedUser();
        SubscribeRequest request = request("work", 10L, null, null, "https://return");
        givenUseCaseReturnsFree(request);

        ResponseEntity<?> response = whenSubscribe(request);

        thenStatusIs(response, HttpStatus.CREATED);
        thenBodyIsNull(response);
    }

    @Test
    @DisplayName("POST /api/payments/subscribe - 200 y body cuando requiere pago")
    void subscribe_shouldReturn200_withPaymentInit_whenPaymentRequired() {
        givenAuthenticatedUser();
        SubscribeRequest request = request("author", 55L, null, "mercadopago", "https://return-url");
        PaymentInitResult init = PaymentInitResult.of(PaymentProviderType.MERCADOPAGO, "https://pay.example/abc", "pref-123");
        givenUseCaseReturnsPayment(request, init);

        ResponseEntity<?> response = whenSubscribe(request);

        thenStatusIs(response, HttpStatus.OK);
        thenBodyIsPaymentInit(response, "mercadopago", "https://pay.example/abc", "pref-123");
    }

    @Test
    @DisplayName("POST /api/payments/subscribe - llama al use case con los parámetros correctos")
    void subscribe_shouldCallUseCase_withCorrectArgs() {
        givenAuthenticatedUser();
        SubscribeRequest request = request("chapter", 77L, 999L, "mp", "http://back");
        givenUseCaseReturnsFreeForAnyArgs();

        whenSubscribe(request);

        thenUseCaseCalledWithArgs(request);
    }

    @Test @DisplayName("subscribe - 400 Invalid subscriptionType")
    void subscribe_shouldReturn400_invalidSubscriptionType() { assertMapsToBadRequest("Invalid subscriptionType"); }
    @Test @DisplayName("subscribe - 400 Cannot subscribe to yourself")
    void subscribe_shouldReturn400_cannotSubscribeToYourself() { assertMapsToBadRequest("Cannot subscribe to yourself"); }
    @Test @DisplayName("subscribe - 400 Author subscription disabled")
    void subscribe_shouldReturn400_authorSubscriptionDisabled() { assertMapsToBadRequest("Author subscription disabled"); }
    @Test @DisplayName("subscribe - 400 Work not found")
    void subscribe_shouldReturn400_workNotFound() { assertMapsToBadRequest("Work not found"); }
    @Test @DisplayName("subscribe - 400 workId is required for chapter subscription")
    void subscribe_shouldReturn400_workIdRequiredForChapter() { assertMapsToBadRequest("workId is required for chapter subscription"); }
    @Test @DisplayName("subscribe - 400 Chapter does not belong to the specified work")
    void subscribe_shouldReturn400_chapterNotBelongToWork() { assertMapsToBadRequest("Chapter does not belong to the specified work"); }
    @Test @DisplayName("subscribe - 400 Provider required")
    void subscribe_shouldReturn400_providerRequired() { assertMapsToBadRequest("Provider required"); }
    @Test @DisplayName("subscribe - 400 Invalid provider")
    void subscribe_shouldReturn400_invalidProvider() { assertMapsToBadRequest("Invalid provider"); }

    @Test
    @DisplayName("subscribe - 412 Payment provider not configured")
    void subscribe_shouldReturn412_providerNotConfigured() {
        givenAuthenticatedUser();
        SubscribeRequest request = anyValidRequest();
        givenUseCaseThrowsIllegalArgument("Payment provider not configured: MERCADOPAGO");

        ResponseEntity<?> response = whenSubscribe(request);

        thenStatusIs(response, HttpStatus.PRECONDITION_FAILED);
        thenBodyEquals(response, "Payment provider not configured: MERCADOPAGO");
    }

    @Test
    @DisplayName("subscribe - 500 en IllegalArgumentException desconocido")
    void subscribe_shouldReturn500_onUnknownIllegalArgument() {
        givenAuthenticatedUser();
        SubscribeRequest request = anyValidRequest();
        givenUseCaseThrowsIllegalArgument("Some other error");

        ResponseEntity<?> response = whenSubscribe(request);

        thenStatusIs(response, HttpStatus.INTERNAL_SERVER_ERROR);
        thenBodyIsNull(response);
    }

    private void assertMapsToBadRequest(String message) {
        givenAuthenticatedUser();
        SubscribeRequest request = anyValidRequest();
        givenUseCaseThrowsIllegalArgument(message);

        ResponseEntity<?> response = whenSubscribe(request);

        thenStatusIs(response, HttpStatus.BAD_REQUEST);
        thenBodyEquals(response, message);
    }
}
