package com.amool.application.usecase;

import com.amool.application.port.out.SaveWorkPort;
import com.amool.application.usecases.SaveWorkUseCase;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveWorkUseCaseTest {

    @Mock
    private SaveWorkPort saveWorkPort;

    @InjectMocks
    private SaveWorkUseCase saveWorkUseCase;

    private final Long TEST_USER_ID = 1L;
    private final Long TEST_WORK_ID = 100L;
    private final Work TEST_WORK;
    
    {
        Work work = new Work();
        work.setId(TEST_WORK_ID);
        work.setTitle("Test Work");
        work.setDescription("Test Description");
        TEST_WORK = work;
    }

    @Test
    void toggleSaveWork_WhenWorkNotSaved_ShouldSaveWork() {
        when(saveWorkPort.isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID)).thenReturn(false);
        
        saveWorkUseCase.toggleSaveWork(TEST_USER_ID, TEST_WORK_ID);
        
        verify(saveWorkPort, times(1)).saveWorkForUser(TEST_USER_ID, TEST_WORK_ID);
        verify(saveWorkPort, never()).removeSavedWorkForUser(any(), any());
    }

    @Test
    void toggleSaveWork_WhenWorkAlreadySaved_ShouldRemoveSavedWork() {
        when(saveWorkPort.isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID)).thenReturn(true);
        
        saveWorkUseCase.toggleSaveWork(TEST_USER_ID, TEST_WORK_ID);
        
        verify(saveWorkPort, times(1)).removeSavedWorkForUser(TEST_USER_ID, TEST_WORK_ID);
        verify(saveWorkPort, never()).saveWorkForUser(any(), any());
    }

    @Test
    void isWorkSavedByUser_ShouldReturnSavedStatus() {
        when(saveWorkPort.isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID)).thenReturn(true);
        
        boolean isSaved = saveWorkUseCase.isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID);
        
        assertTrue(isSaved);
        verify(saveWorkPort, times(1)).isWorkSavedByUser(TEST_USER_ID, TEST_WORK_ID);
    }

    @Test
    void getSavedWorksByUser_ShouldReturnListOfSavedWorks() {
        Work work1 = new Work();
        work1.setId(1L);
        work1.setTitle("Work 1");
        work1.setDescription("Description 1");
        
        Work work2 = new Work();
        work2.setId(2L);
        work2.setTitle("Work 2");
        work2.setDescription("Description 2");
        
        List<Work> expectedWorks = Arrays.asList(work1, work2);
        when(saveWorkPort.getSavedWorksByUser(TEST_USER_ID)).thenReturn(expectedWorks);
        
        List<Work> result = saveWorkUseCase.getSavedWorksByUser(TEST_USER_ID);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedWorks, result);
        verify(saveWorkPort, times(1)).getSavedWorksByUser(TEST_USER_ID);
    }

    @Test
    void getSavedWorksByUser_WhenNoSavedWorks_ShouldReturnEmptyList() {
        when(saveWorkPort.getSavedWorksByUser(TEST_USER_ID)).thenReturn(List.of());
        
        List<Work> result = saveWorkUseCase.getSavedWorksByUser(TEST_USER_ID);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(saveWorkPort, times(1)).getSavedWorksByUser(TEST_USER_ID);
    }
}
