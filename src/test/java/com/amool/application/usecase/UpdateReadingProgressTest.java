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
    public void when_CreateProgressSucceeds_ThenReturnTrueAndAddToHistory() {
        when(readingProgressPort.create(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(true);

        boolean result = updateReadingProgress.execute(USER_ID, WORK_ID, CHAPTER_ID);

        assertTrue(result);
        verify(readingProgressPort, times(1)).addToHistory(USER_ID, WORK_ID, CHAPTER_ID);
    }


    @Test
    public void when_CreateProgressFails_ThenReturnFalseAndDoNotAddToHistory() {
        when(readingProgressPort.create(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(false);

        boolean result = updateReadingProgress.execute(USER_ID, WORK_ID, CHAPTER_ID);

        assertFalse(result);
        verify(readingProgressPort, times(0)).addToHistory(USER_ID, WORK_ID, CHAPTER_ID);
    }
}
