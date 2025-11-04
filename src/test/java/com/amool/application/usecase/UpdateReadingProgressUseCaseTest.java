package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.amool.application.port.out.ReadingProgressPort;
import com.amool.application.usecases.UpdateReadingProgressUseCase;

public class UpdateReadingProgressUseCaseTest {

    private UpdateReadingProgressUseCase updateReadingProgressUseCase;
    private ReadingProgressPort readingProgressPort;

    private static final Long USER_ID = 1L;
    private static final Long WORK_ID = 2L;
    private static final Long CHAPTER_ID = 3L;
    
    @BeforeEach
    public void setUp() {
        readingProgressPort = Mockito.mock(ReadingProgressPort.class);
        updateReadingProgressUseCase = new UpdateReadingProgressUseCase(readingProgressPort);
    }

    @Test
    public void when_UpdateExistingProgress_ThenReturnTrue() {
        when(readingProgressPort.update(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(true);

        boolean result = updateReadingProgressUseCase.execute(USER_ID, WORK_ID, CHAPTER_ID);

        assertTrue(result);
    }
    

    @Test
    public void when_ProgressDoesNotExist_ThenCreateNewProgressAndReturnTrue() {
        when(readingProgressPort.update(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(false);
        when(readingProgressPort.create(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(true);

        boolean result = updateReadingProgressUseCase.execute(USER_ID, WORK_ID, CHAPTER_ID);

        assertTrue(result);
    }

    @Test
    public void when_UpdateFailsAndCreateFails_ThenReturnFalse() {
        when(readingProgressPort.update(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(false);
        when(readingProgressPort.create(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(false);

        boolean result = updateReadingProgressUseCase.execute(USER_ID, WORK_ID, CHAPTER_ID);

        assertFalse(result);
    }
}
