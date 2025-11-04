package com.amool.adapters.in.rest.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amool.adapters.in.rest.dtos.ReadingProgressDto;
import com.amool.application.port.out.ReadingProgressPort;
import com.amool.application.usecases.UpdateReadingProgressUseCase;

public class ReadingProgressControllerTest {

    private ReadingProgressController readingProgressController;
    private UpdateReadingProgressUseCase updateReadingProgressUseCase;
    private ReadingProgressPort readingProgressPort;
    
    private static final Long USER_ID = 1L;
    private static final Long WORK_ID = 2L;
    private static final Long CHAPTER_ID = 3L;
    
    @BeforeEach
    public void setUp() {
        readingProgressPort = Mockito.mock(ReadingProgressPort.class);
        updateReadingProgressUseCase = new UpdateReadingProgressUseCase(readingProgressPort);
        readingProgressController = new ReadingProgressController(updateReadingProgressUseCase);
    }

    @Test
    public void when_UpdateReadingProgress_ThenReturnOk() {
        when(readingProgressPort.update(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(true);
        
        ResponseEntity<Void> response = readingProgressController.updateReadingProgress(new ReadingProgressDto(USER_ID, WORK_ID, CHAPTER_ID));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void when_UpdateProgressFails_ThenReturnFalse() {
        when(readingProgressPort.update(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(false);
        
        ResponseEntity<Void> response = readingProgressController.updateReadingProgress(new ReadingProgressDto(USER_ID, WORK_ID, CHAPTER_ID));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
}
