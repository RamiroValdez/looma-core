package com.amool.application.usecase;

import com.amool.application.port.out.WorkPort;
import com.amool.application.usecases.GetAllWorks;
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
class GetAllWorksTest {

    @Mock
    private WorkPort workPort;

    @InjectMocks
    private GetAllWorks getAllWorks;

    @Test
    void execute_shouldReturnListOfWorks() {
        Work work1 = new Work();
        work1.setId(1L);
        work1.setTitle("Work 1");
        
        Work work2 = new Work();
        work2.setId(2L);
        work2.setTitle("Work 2");
        
        List<Work> expectedWorks = Arrays.asList(work1, work2);
        
        when(workPort.getAllWorks()).thenReturn(expectedWorks);

        List<Work> result = getAllWorks.execute();

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return 2 works");
        assertEquals("Work 1", result.get(0).getTitle(), "First work title should match");
        assertEquals("Work 2", result.get(1).getTitle(), "Second work title should match");
        
        verify(workPort, times(1)).getAllWorks();
    }

    @Test
    void execute_whenNoWorksExist_shouldReturnEmptyList() {
        when(workPort.getAllWorks()).thenReturn(List.of());

        List<Work> result = getAllWorks.execute();

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be an empty list");
        
        verify(workPort, times(1)).getAllWorks();
    }
}
