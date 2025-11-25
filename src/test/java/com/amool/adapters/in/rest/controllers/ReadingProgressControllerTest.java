package com.amool.adapters.in.rest.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amool.adapters.in.rest.dtos.ReadingProgressDto;
import com.amool.application.port.out.ReadingProgressPort;
import com.amool.application.usecases.UpdateReadingProgress;

public class ReadingProgressControllerTest {

    private ReadingProgressController readingProgressController;
    private UpdateReadingProgress updateReadingProgress;
    private ReadingProgressPort readingProgressPort;
    
    private static final Long USER_ID = 1L;
    private static final Long WORK_ID = 2L;
    private static final Long CHAPTER_ID = 3L;
    
    @BeforeEach
    public void setUp() {
        readingProgressPort = Mockito.mock(ReadingProgressPort.class);
        updateReadingProgress = new UpdateReadingProgress(readingProgressPort);
        readingProgressController = new ReadingProgressController(updateReadingProgress);
    }

    @Test
    public void when_CreateReadingProgressSucceeds_ThenReturnOkAndAddToHistory() {
        when(readingProgressPort.create(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(true);
        
        ResponseEntity<Void> response = readingProgressController.updateReadingProgress(new ReadingProgressDto(USER_ID, WORK_ID, CHAPTER_ID));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(readingProgressPort, times(1)).addToHistory(USER_ID, WORK_ID, CHAPTER_ID);
    }

    @Test
    public void when_CreateReadingProgressFails_ThenReturnBadRequestAndDoNotAddToHistory() {
        when(readingProgressPort.create(USER_ID, WORK_ID, CHAPTER_ID))
            .thenReturn(false);
        
        ResponseEntity<Void> response = readingProgressController.updateReadingProgress(new ReadingProgressDto(USER_ID, WORK_ID, CHAPTER_ID));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(readingProgressPort, times(0)).addToHistory(USER_ID, WORK_ID, CHAPTER_ID);
    }
    
}
