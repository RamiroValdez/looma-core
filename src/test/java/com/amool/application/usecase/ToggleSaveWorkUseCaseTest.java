package com.amool.application.usecase;

import com.amool.application.port.out.SaveWorkPort;
import com.amool.application.usecases.ToggleSaveWorkUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleSaveWorkUseCaseTest {

    @Mock
    private SaveWorkPort saveWorkPort;

    @InjectMocks
    private ToggleSaveWorkUseCase toggleSaveWorkUseCase;

    private final Long TEST_USER_ID = 1L;
    private final Long TEST_WORK_ID = 100L;

    @Test
    void execute_WhenWorkNotSaved_ShouldSaveWork() {
        when(saveWorkPort.isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID)).thenReturn(false);

        toggleSaveWorkUseCase.execute(TEST_USER_ID, TEST_WORK_ID);

        verify(saveWorkPort, times(1)).isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID);
        verify(saveWorkPort, times(1)).saveWorkForUser(TEST_USER_ID, TEST_WORK_ID);
        verify(saveWorkPort, never()).removeSavedWorkForUser(any(), any());
    }

    @Test
    void execute_WhenWorkAlreadySaved_ShouldRemoveSavedWork() {
        when(saveWorkPort.isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID)).thenReturn(true);

        toggleSaveWorkUseCase.execute(TEST_USER_ID, TEST_WORK_ID);

        verify(saveWorkPort, times(1)).isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID);
        verify(saveWorkPort, times(1)).removeSavedWorkForUser(TEST_USER_ID, TEST_WORK_ID);
        verify(saveWorkPort, never()).saveWorkForUser(any(), any());
    }
}