package com.amool.application.usecase;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;
import com.amool.application.usecases.ToggleWorkLikeUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleWorkLikeTest {

    @Mock
    private LikePort likePort;

    @InjectMocks
    private ToggleWorkLikeUseCase toggleWorkLikeUseCase;

    private static final Long WORK_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long INITIAL_LIKES = 5L;

    @Test
    void execute_WhenUserHasNotLikedWork_ShouldAddLike() {
        givenUserHasNotLikedWork();

        LikeResponseDto response = whenToggleLikeIsExecuted();

        thenLikeShouldBeAdded(response);
        thenLikeOperationShouldBeVerified();
    }

    @Test
    void execute_WhenUserHasLikedWork_ShouldRemoveLike() {
        givenUserHasLikedWork();

        LikeResponseDto response = whenToggleLikeIsExecuted();

        thenLikeShouldBeRemoved(response);
        thenUnlikeOperationShouldBeVerified();
    }

    @Test
    void execute_WhenFirstLikeOnWork_ShouldReturnOne() {
        givenWorkHasNoLikes();

        LikeResponseDto response = whenToggleLikeIsExecuted();

        thenFirstLikeShouldBeAdded(response);
    }

    @Test
    void execute_WhenLastLikeRemoved_ShouldReturnZero() {
        givenWorkHasOnlyOneLikeFromUser();

        LikeResponseDto response = whenToggleLikeIsExecuted();

        thenLastLikeShouldBeRemoved(response);
    }

    @Test
    void execute_ShouldUseProvidedWorkId() {
        Long differentWorkId = 999L;
        givenUserHasNotLikedWorkForSpecificWork(differentWorkId);

        LikeResponseDto response = whenToggleLikeIsExecutedForSpecificWork(differentWorkId);

        thenWorkIdShouldMatch(response, differentWorkId);
        thenLikeOperationShouldBeVerifiedForSpecificWork(differentWorkId);
    }

    @Test
    void execute_ShouldUseProvidedUserId() {
        Long differentUserId = 999L;
        givenSpecificUserHasNotLikedWork(differentUserId);

        LikeResponseDto response = whenToggleLikeIsExecutedForSpecificUser(differentUserId);

        thenLikeShouldBeAddedForSpecificUser(response);
        thenLikeOperationShouldBeVerifiedForSpecificUser(differentUserId);
    }

    @Test
    void execute_WhenToggleMultipleTimes_ShouldWorkCorrectly() {
        givenMultipleToggleScenario();

        LikeResponseDto firstResponse = whenToggleLikeIsExecuted();
        LikeResponseDto secondResponse = whenToggleLikeIsExecuted();

        thenFirstToggleShouldAddLike(firstResponse);
        thenSecondToggleShouldRemoveLike(secondResponse);
        thenMultipleToggleOperationsShouldBeVerified();
    }

    @Test
    void execute_WithNullWorkId_ShouldThrowException() {

        Long nullWorkId = givenNullWorkId();


        IllegalArgumentException exception = whenToggleLikeIsExecutedWithNullWorkId(nullWorkId);

        thenExceptionMessageShouldBe(exception, "Work ID cannot be null");
    }

    @Test
    void execute_WithNullUserId_ShouldThrowException() {

        Long nullUserId = givenNullUserId();


        IllegalArgumentException exception = whenToggleLikeIsExecutedWithNullUserId(nullUserId);

        thenExceptionMessageShouldBe(exception, "User ID cannot be null");
    }

    @Test
    void execute_WhenPortThrowsException_ShouldPropagateException() {

        givenPortThrowsException();

        RuntimeException exception = whenToggleLikeIsExecutedAndPortFails();

        thenExceptionMessageShouldBe(exception, "Database error");
    }

    @Test
    void execute_WhenLikeOperationThrowsException_ShouldPropagateException() {

        givenLikeOperationThrowsException();

        RuntimeException exception = whenToggleLikeIsExecutedAndLikeOperationFails();

        thenExceptionMessageShouldBe(exception, "Like operation failed");
        thenLikeOperationAttemptShouldBeVerified();
    }

    @Test
    void execute_WhenUnlikeOperationThrowsException_ShouldPropagateException() {

        givenUnlikeOperationThrowsException();

        RuntimeException exception = whenToggleLikeIsExecutedAndUnlikeOperationFails();

        thenExceptionMessageShouldBe(exception, "Unlike operation failed");
        thenUnlikeOperationAttemptShouldBeVerified();
    }

    private void givenUserHasNotLikedWork() {
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID)).thenReturn(false);
        when(likePort.likeWork(WORK_ID, USER_ID)).thenReturn(INITIAL_LIKES + 1);
    }

    private void givenUserHasLikedWork() {
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID)).thenReturn(true);
        when(likePort.unlikeWork(WORK_ID, USER_ID)).thenReturn(INITIAL_LIKES - 1);
    }

    private void givenWorkHasNoLikes() {
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID)).thenReturn(false);
        when(likePort.likeWork(WORK_ID, USER_ID)).thenReturn(1L);
    }

    private void givenWorkHasOnlyOneLikeFromUser() {
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID)).thenReturn(true);
        when(likePort.unlikeWork(WORK_ID, USER_ID)).thenReturn(0L);
    }

    private void givenUserHasNotLikedWorkForSpecificWork(Long workId) {
        when(likePort.hasUserLikedWork(workId, USER_ID)).thenReturn(false);
        when(likePort.likeWork(workId, USER_ID)).thenReturn(1L);
    }

    private void givenSpecificUserHasNotLikedWork(Long userId) {
        when(likePort.hasUserLikedWork(WORK_ID, userId)).thenReturn(false);
        when(likePort.likeWork(WORK_ID, userId)).thenReturn(1L);
    }

    private void givenMultipleToggleScenario() {
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID))
                .thenReturn(false) // Primera llamada: no tiene like
                .thenReturn(true); // Segunda llamada: ya tiene like
        when(likePort.likeWork(WORK_ID, USER_ID)).thenReturn(1L);
        when(likePort.unlikeWork(WORK_ID, USER_ID)).thenReturn(0L);
    }

    private Long givenNullWorkId() {
        return null;
    }

    private Long givenNullUserId() {
        return null;
    }

    private void givenPortThrowsException() {
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID))
                .thenThrow(new RuntimeException("Database error"));
    }

    private void givenLikeOperationThrowsException() {
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID)).thenReturn(false);
        when(likePort.likeWork(WORK_ID, USER_ID))
                .thenThrow(new RuntimeException("Like operation failed"));
    }

    private void givenUnlikeOperationThrowsException() {
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID)).thenReturn(true);
        when(likePort.unlikeWork(WORK_ID, USER_ID))
                .thenThrow(new RuntimeException("Unlike operation failed"));
    }

    private LikeResponseDto whenToggleLikeIsExecuted() {
        return toggleWorkLikeUseCase.execute(WORK_ID, USER_ID);
    }

    private LikeResponseDto whenToggleLikeIsExecutedForSpecificWork(Long workId) {
        return toggleWorkLikeUseCase.execute(workId, USER_ID);
    }

    private LikeResponseDto whenToggleLikeIsExecutedForSpecificUser(Long userId) {
        return toggleWorkLikeUseCase.execute(WORK_ID, userId);
    }

    private IllegalArgumentException whenToggleLikeIsExecutedWithNullWorkId(Long nullWorkId) {
        return assertThrows(IllegalArgumentException.class,
            () -> toggleWorkLikeUseCase.execute(nullWorkId, USER_ID));
    }

    private IllegalArgumentException whenToggleLikeIsExecutedWithNullUserId(Long nullUserId) {
        return assertThrows(IllegalArgumentException.class,
            () -> toggleWorkLikeUseCase.execute(WORK_ID, nullUserId));
    }

    private RuntimeException whenToggleLikeIsExecutedAndPortFails() {
        return assertThrows(RuntimeException.class,
            () -> toggleWorkLikeUseCase.execute(WORK_ID, USER_ID));
    }

    private RuntimeException whenToggleLikeIsExecutedAndLikeOperationFails() {
        return assertThrows(RuntimeException.class,
            () -> toggleWorkLikeUseCase.execute(WORK_ID, USER_ID));
    }

    private RuntimeException whenToggleLikeIsExecutedAndUnlikeOperationFails() {
        return assertThrows(RuntimeException.class,
            () -> toggleWorkLikeUseCase.execute(WORK_ID, USER_ID));
    }

    private void thenLikeShouldBeAdded(LikeResponseDto response) {
        assertEquals(WORK_ID, response.getWorkId());
        assertEquals(INITIAL_LIKES + 1, response.getLikeCount());
        assertTrue(response.isLikedByUser());
    }

    private void thenLikeOperationShouldBeVerified() {
        verify(likePort).hasUserLikedWork(WORK_ID, USER_ID);
        verify(likePort).likeWork(WORK_ID, USER_ID);
        verify(likePort, never()).unlikeWork(WORK_ID, USER_ID);
    }

    private void thenLikeShouldBeRemoved(LikeResponseDto response) {
        assertEquals(WORK_ID, response.getWorkId());
        assertEquals(INITIAL_LIKES - 1, response.getLikeCount());
        assertFalse(response.isLikedByUser());
    }

    private void thenUnlikeOperationShouldBeVerified() {
        verify(likePort).hasUserLikedWork(WORK_ID, USER_ID);
        verify(likePort).unlikeWork(WORK_ID, USER_ID);
        verify(likePort, never()).likeWork(WORK_ID, USER_ID);
    }

    private void thenFirstLikeShouldBeAdded(LikeResponseDto response) {
        assertEquals(WORK_ID, response.getWorkId());
        assertEquals(1L, response.getLikeCount());
        assertTrue(response.isLikedByUser());
    }

    private void thenLastLikeShouldBeRemoved(LikeResponseDto response) {
        assertEquals(WORK_ID, response.getWorkId());
        assertEquals(0L, response.getLikeCount());
        assertFalse(response.isLikedByUser());
    }

    private void thenWorkIdShouldMatch(LikeResponseDto response, Long expectedWorkId) {
        assertEquals(expectedWorkId, response.getWorkId());
    }

    private void thenLikeOperationShouldBeVerifiedForSpecificWork(Long workId) {
        verify(likePort).hasUserLikedWork(workId, USER_ID);
        verify(likePort).likeWork(workId, USER_ID);
    }

    private void thenLikeShouldBeAddedForSpecificUser(LikeResponseDto response) {
        assertEquals(WORK_ID, response.getWorkId());
        assertTrue(response.isLikedByUser());
    }

    private void thenLikeOperationShouldBeVerifiedForSpecificUser(Long userId) {
        verify(likePort).hasUserLikedWork(WORK_ID, userId);
        verify(likePort).likeWork(WORK_ID, userId);
    }

    private void thenFirstToggleShouldAddLike(LikeResponseDto response) {
        assertTrue(response.isLikedByUser());
        assertEquals(1L, response.getLikeCount());
    }

    private void thenSecondToggleShouldRemoveLike(LikeResponseDto response) {
        assertFalse(response.isLikedByUser());
        assertEquals(0L, response.getLikeCount());
    }

    private void thenMultipleToggleOperationsShouldBeVerified() {
        verify(likePort, times(2)).hasUserLikedWork(WORK_ID, USER_ID);
        verify(likePort).likeWork(WORK_ID, USER_ID);
        verify(likePort).unlikeWork(WORK_ID, USER_ID);
    }

    private void thenExceptionMessageShouldBe(Exception exception, String expectedMessage) {
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void thenLikeOperationAttemptShouldBeVerified() {
        verify(likePort).hasUserLikedWork(WORK_ID, USER_ID);
        verify(likePort).likeWork(WORK_ID, USER_ID);
    }

    private void thenUnlikeOperationAttemptShouldBeVerified() {
        verify(likePort).hasUserLikedWork(WORK_ID, USER_ID);
        verify(likePort).unlikeWork(WORK_ID, USER_ID);
    }
}
