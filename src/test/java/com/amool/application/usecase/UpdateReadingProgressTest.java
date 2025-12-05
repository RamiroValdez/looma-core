package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.amool.application.port.out.ReadingProgressPort;
import com.amool.application.usecases.UpdateReadingProgress;

public class UpdateReadingProgressTest {

    private UpdateReadingProgress updateReadingProgress;
    private ReadingProgressPort readingProgressPort;

    private static final Long USER_ID = 1L;
    private static final Long WORK_ID = 2L;
    private static final Long CHAPTER_ID = 3L;
    
    @BeforeEach
    public void setUp() {
        readingProgressPort = Mockito.mock(ReadingProgressPort.class);
        updateReadingProgress = new UpdateReadingProgress(readingProgressPort);
    }

    @Test
    public void shouldAddToHistoryWhenCreateSucceeds() {
        givenCreateProgressResult(true);

        boolean result = whenUpdatingProgress();

        thenUpdateSucceeded(result);
        thenHistoryUpdated();
    }

    @Test
    public void shouldNotAddToHistoryWhenCreateFails() {
        givenCreateProgressResult(false);

        boolean result = whenUpdatingProgress();

        thenUpdateFailed(result);
        thenHistoryNotUpdated();
    }

    private void givenCreateProgressResult(boolean created) {
        when(readingProgressPort.create(USER_ID, WORK_ID, CHAPTER_ID)).thenReturn(created);
    }

    private boolean whenUpdatingProgress() {
        return updateReadingProgress.execute(USER_ID, WORK_ID, CHAPTER_ID);
    }

    private void thenUpdateSucceeded(boolean result) {
        assertTrue(result);
    }

    private void thenUpdateFailed(boolean result) {
        assertFalse(result);
    }

    private void thenHistoryUpdated() {
        verify(readingProgressPort).addToHistory(USER_ID, WORK_ID, CHAPTER_ID);
    }

    private void thenHistoryNotUpdated() {
        verify(readingProgressPort, times(0)).addToHistory(USER_ID, WORK_ID, CHAPTER_ID);
    }
}
