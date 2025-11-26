package com.amool.application.usecase;

import com.amool.application.port.out.SaveWorkPort;
import com.amool.application.usecases.IsWorkSaved;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IsWorkSavedTest {

    @Mock
    private SaveWorkPort saveWorkPort;

    @InjectMocks
    private IsWorkSaved isWorkSaved;

    private final Long TEST_USER_ID = 1L;
    private final Long TEST_WORK_ID = 100L;

    private void givenWorkIsSaved(Long userId, Long workId) {
        when(saveWorkPort.isWorkSavedByUser(userId, workId)).thenReturn(true);
    }

    private void givenWorkIsNotSaved(Long userId, Long workId) {
        when(saveWorkPort.isWorkSavedByUser(userId, workId)).thenReturn(false);
    }

    private boolean whenCheckIsWorkSaved(Long userId, Long workId) {
        return isWorkSaved.execute(userId, workId);
    }

    private void thenResultIsTrue(boolean result) { assertTrue(result); }
    private void thenResultIsFalse(boolean result) { assertFalse(result); }
    private void thenPortCalledOnce(Long userId, Long workId) { verify(saveWorkPort, times(1)).isWorkSavedByUser(userId, workId); }

    @Test
    void execute_WhenWorkIsSaved_ShouldReturnTrue() {
        givenWorkIsSaved(TEST_USER_ID, TEST_WORK_ID);

        boolean result = whenCheckIsWorkSaved(TEST_USER_ID, TEST_WORK_ID);

        thenResultIsTrue(result);
        thenPortCalledOnce(TEST_USER_ID, TEST_WORK_ID);
    }

    @Test
    void execute_WhenWorkIsNotSaved_ShouldReturnFalse() {
        givenWorkIsNotSaved(TEST_USER_ID, TEST_WORK_ID);

        boolean result = whenCheckIsWorkSaved(TEST_USER_ID, TEST_WORK_ID);

        thenResultIsFalse(result);
        thenPortCalledOnce(TEST_USER_ID, TEST_WORK_ID);
    }
}
