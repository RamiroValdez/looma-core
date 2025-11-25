package com.amool.application.usecase;

import com.amool.application.port.out.SaveWorkPort;
import com.amool.application.usecases.GetSavedWorks;
import com.amool.domain.model.Work;
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
class GetSavedWorksTest {

    @Mock
    private SaveWorkPort saveWorkPort;

    @InjectMocks
    private GetSavedWorks getSavedWorks;

    private final Long TEST_USER_ID = 1L;

    @Test
    void execute_ShouldReturnListOfSavedWorks() {
        Work work1 = new Work();
        work1.setId(1L);
        work1.setTitle("Work 1");
        
        Work work2 = new Work();
        work2.setId(2L);
        work2.setTitle("Work 2");
        
        List<Work> expectedWorks = Arrays.asList(work1, work2);
        when(saveWorkPort.getSavedWorksByUser(TEST_USER_ID)).thenReturn(expectedWorks);
        
        List<Work> result = getSavedWorks.execute(TEST_USER_ID);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedWorks, result);
        verify(saveWorkPort, times(1)).getSavedWorksByUser(TEST_USER_ID);
    }

    @Test
    void execute_WhenNoSavedWorks_ShouldReturnEmptyList() {
        when(saveWorkPort.getSavedWorksByUser(TEST_USER_ID)).thenReturn(List.of());
        
        List<Work> result = getSavedWorks.execute(TEST_USER_ID);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(saveWorkPort, times(1)).getSavedWorksByUser(TEST_USER_ID);
    }
}
