package com.amool.application.usecase;

import com.amool.application.port.out.SaveWorkPort;
import com.amool.application.usecases.ToggleSaveWork;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleSaveWorkTest {

    @Mock
    private SaveWorkPort saveWorkPort;

    @InjectMocks
    private ToggleSaveWork toggleSaveWork;

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_WORK_ID = 100L;

    @Test
    void shouldSaveWorkWhenNotPreviouslySaved() {
        givenWorkSavedState(false);

        whenTogglingSave();

        thenSaveWorkTriggered();
        thenRemoveWorkNotTriggered();
    }

    @Test
    void shouldRemoveWorkWhenAlreadySaved() {
        givenWorkSavedState(true);

        whenTogglingSave();

        thenRemoveWorkTriggered();
        thenSaveWorkNotTriggered();
    }

    private void givenWorkSavedState(boolean isSaved) {
        when(saveWorkPort.isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID)).thenReturn(isSaved);
    }

    private void whenTogglingSave() {
        toggleSaveWork.execute(TEST_USER_ID, TEST_WORK_ID);
    }

    private void thenSaveWorkTriggered() {
        verify(saveWorkPort).isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID);
        verify(saveWorkPort).saveWorkForUser(TEST_USER_ID, TEST_WORK_ID);
    }

    private void thenRemoveWorkTriggered() {
        verify(saveWorkPort).isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID);
        verify(saveWorkPort).removeSavedWorkForUser(TEST_USER_ID, TEST_WORK_ID);
    }

    private void thenSaveWorkNotTriggered() {
        verify(saveWorkPort, never()).saveWorkForUser(any(), any());
    }

    private void thenRemoveWorkNotTriggered() {
        verify(saveWorkPort, never()).removeSavedWorkForUser(any(), any());
    }
}