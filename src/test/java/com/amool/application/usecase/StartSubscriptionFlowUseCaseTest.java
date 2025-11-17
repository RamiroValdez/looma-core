package com.amool.application.usecase;

import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.PaymentProviderPort;
import com.amool.application.usecases.StartSubscriptionFlowUseCase;
import com.amool.application.usecases.SubscribeUserUseCase;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.PaymentInitResult;
import com.amool.domain.model.PaymentProviderType;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartSubscriptionFlowUseCaseTest {

    private static final Long USER_ID = 10L;

    @Mock
    private ObtainWorkByIdPort obtainWorkByIdPort;

    @Mock
    private LoadChapterPort loadChapterPort;

    @Mock
    private SubscribeUserUseCase subscribeUserUseCase;

    @Mock
    private PaymentProviderPort paymentProvider;

    private StartSubscriptionFlowUseCase useCaseWithAuthorPrice(BigDecimal authorPrice, List<PaymentProviderPort> providers) {
        return new StartSubscriptionFlowUseCase(
                obtainWorkByIdPort,
                loadChapterPort,
                subscribeUserUseCase,
                providers,
                authorPrice
        );
    }

    @BeforeEach
    void resetMocks() {
        clearInvocations(obtainWorkByIdPort, loadChapterPort, subscribeUserUseCase, paymentProvider);
    }

    @Test
    void invalidSubscriptionType_shouldThrow() {
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.ONE, List.of());
        expectIAE("Invalid subscriptionType", () -> uc.execute(USER_ID, "nope", 1L, null, null, null));
    }

    @Test
    void authorSelfSubscription_shouldThrow() {
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.ONE, List.of());
        expectIAE("Cannot subscribe to yourself", () -> executeAuthor(uc, USER_ID, null, null));
    }

    @Test
    void authorSubscriptionDisabled_whenAuthorPriceNull_shouldThrow() {
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(null, List.of());
        expectIAE("Author subscription disabled", () -> executeAuthor(uc, 99L, null, null));
    }

    @Test
    void workNotFound_shouldThrow() {
        givenWorkNotFound(77L);
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.ONE, List.of());

        expectIAE("Work not found", () -> executeWork(uc, 77L, null, null));
        verify(obtainWorkByIdPort).obtainWorkById(77L);
        verifyNoInteractions(loadChapterPort, subscribeUserUseCase);
    }

    @Test
    void chapterMissingWorkId_shouldThrow() {
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.ONE, List.of());
        expectIAE("workId is required for chapter subscription", () -> uc.execute(USER_ID, "chapter", 5L, null, null, null));
        verifyNoInteractions(loadChapterPort, subscribeUserUseCase, obtainWorkByIdPort);
    }

    @Test
    void chapterDoesNotBelongToWork_shouldThrow() {
        givenChapterNotFound(1L, 2L);
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.ONE, List.of());

        expectIAE("Chapter does not belong to the specified work", () -> executeChapter(uc, 2L, 1L, null, null));
        verify(loadChapterPort).loadChapter(1L, 2L);
        verifyNoInteractions(subscribeUserUseCase, obtainWorkByIdPort);
    }

    @Test
    void freeFlow_workPriceZero_shouldSubscribeAndReturnFree() {
        givenWorkPrice(55L, BigDecimal.ZERO);
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.TEN, List.of());

        StartSubscriptionFlowUseCase.Result result = executeWork(uc, 55L, null, null);

        assertFree(result);
        thenSubscribedTo(com.amool.domain.model.SubscriptionType.WORK, 55L);
        verifyNoInteractions(loadChapterPort);
    }

    @Test
    void paymentFlow_providerRequired_whenPricePositive() {
        givenWorkPrice(55L, BigDecimal.valueOf(100));
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.TEN, List.of());

        expectIAE("Provider required", () -> executeWork(uc, 55L, null, null));
        verifyNoInteractions(subscribeUserUseCase);
    }

    @Test
    void paymentFlow_invalidProvider_shouldThrow() {
        givenWorkPrice(55L, BigDecimal.valueOf(100));
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.TEN, List.of());

        expectIAE("Invalid provider", () -> executeWork(uc, 55L, "bad", null));
        verifyNoInteractions(subscribeUserUseCase);
    }

    @Test
    void paymentFlow_providerNotConfigured_shouldThrow() {
        givenWorkPrice(55L, BigDecimal.valueOf(100));
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.TEN, List.of());

        expectIAE("Payment provider not configured: MERCADOPAGO", () -> executeWork(uc, 55L, "mp", null));
    }

    @Test
    void paymentFlow_success_shouldReturnPaymentInit() {
        givenWorkPrice(55L, BigDecimal.valueOf(150));

        when(paymentProvider.supportedProvider()).thenReturn(PaymentProviderType.MERCADOPAGO);
        PaymentInitResult expected = PaymentInitResult.of(PaymentProviderType.MERCADOPAGO, "https://pay", "ref-123");
        when(paymentProvider.startCheckout(eq(USER_ID), eq(com.amool.domain.model.SubscriptionType.WORK), eq(55L), any()))
                .thenReturn(expected);

        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.TEN, List.of(paymentProvider));

        StartSubscriptionFlowUseCase.Result result = executeWork(uc, 55L, "mercadopago", "https://return");

        assertFalse(result.isFree());
        assertNotNull(result.getPaymentInit());
        assertEquals(expected, result.getPaymentInit());

        thenCheckoutStarted(com.amool.domain.model.SubscriptionType.WORK, 55L, "https://return");
        verifyNoInteractions(subscribeUserUseCase);
    }

    @Test
    void chapterPriceZero_shouldSubscribeFree() {
        givenChapterPrice(9L, 3L, BigDecimal.ZERO);
        StartSubscriptionFlowUseCase uc = useCaseWithAuthorPrice(BigDecimal.TEN, List.of());

        StartSubscriptionFlowUseCase.Result result = executeChapter(uc, 3L, 9L, null, null);

        assertTrue(result.isFree());
        thenSubscribedTo(com.amool.domain.model.SubscriptionType.CHAPTER, 3L);
    }

    // Helpers: Given
    private void givenWorkPrice(long workId, BigDecimal price) {
        Work work = new Work();
        work.setPrice(price);
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));
    }

    private void givenWorkNotFound(long workId) {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.empty());
    }

    private void givenChapterPrice(long workId, long chapterId, BigDecimal price) {
        Chapter chapter = new Chapter();
        chapter.setPrice(price);
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.of(chapter));
    }

    private void givenChapterNotFound(long workId, long chapterId) {
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.empty());
    }

    // Helpers: When
    private StartSubscriptionFlowUseCase.Result executeWork(StartSubscriptionFlowUseCase uc, long workId, String provider, String returnUrl) {
        return uc.execute(USER_ID, "work", workId, null, provider, returnUrl);
    }

    private StartSubscriptionFlowUseCase.Result executeChapter(StartSubscriptionFlowUseCase uc, long chapterId, long workId, String provider, String returnUrl) {
        return uc.execute(USER_ID, "chapter", chapterId, workId, provider, returnUrl);
    }

    private StartSubscriptionFlowUseCase.Result executeAuthor(StartSubscriptionFlowUseCase uc, long authorId, String provider, String returnUrl) {
        return uc.execute(USER_ID, "author", authorId, null, provider, returnUrl);
    }

    // Helpers: Then / Assertions
    private void assertFree(StartSubscriptionFlowUseCase.Result result) {
        assertTrue(result.isFree());
        assertNull(result.getPaymentInit());
    }

    private void thenSubscribedTo(com.amool.domain.model.SubscriptionType type, long targetId) {
        verify(subscribeUserUseCase).execute(USER_ID, type, targetId);
    }

    private void thenCheckoutStarted(com.amool.domain.model.SubscriptionType type, long targetId, String returnUrl) {
        verify(paymentProvider).startCheckout(eq(USER_ID), eq(type), eq(targetId), eq(returnUrl));
    }

    private void expectIAE(String message, Executable executable) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(message, ex.getMessage());
    }
}
