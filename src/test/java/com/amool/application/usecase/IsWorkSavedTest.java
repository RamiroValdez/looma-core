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

    @Test
    void execute_WhenWorkIsSaved_ShouldReturnTrue() {
        when(saveWorkPort.isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID)).thenReturn(true);
        
        boolean result = isWorkSaved.execute(TEST_USER_ID, TEST_WORK_ID);
        
        assertTrue(result);
        verify(saveWorkPort, times(1)).isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID);
    }

    @Test
    void execute_WhenWorkIsNotSaved_ShouldReturnFalse() {
        when(saveWorkPort.isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID)).thenReturn(false);
        
        boolean result = isWorkSaved.execute(TEST_USER_ID, TEST_WORK_ID);
        
        assertFalse(result);
        verify(saveWorkPort, times(1)).isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID);
    }
}
