package com.amool.hexagonal.application.port.service;

import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ObtainWorkByIdServiceTest {

    @Mock
    private ObtainWorkByIdPort obtainWorkByIdPort;

    @InjectMocks
    private ObtainWorkByIdService obtainWorkByIdService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExecute_ShouldReturnWork_WhenWorkExists() {
        Long workId = 1L;
        Work expectedWork = new Work();
        expectedWork.setId(workId);
        expectedWork.setTitle("Test Work");
        
        when(obtainWorkByIdPort.execute(workId)).thenReturn(expectedWork);

        Work result = obtainWorkByIdService.execute(workId);

        assertNotNull(result);
        assertEquals(workId, result.getId());
        assertEquals("Test Work", result.getTitle());
        verify(obtainWorkByIdPort, times(1)).execute(workId);
    }

    @Test
    public void testExecute_ShouldReturnNull_WhenWorkDoesNotExist() {
        Long workId = 999L;
        when(obtainWorkByIdPort.execute(workId)).thenReturn(null);

        Work result = obtainWorkByIdService.execute(workId);

        assertNull(result);
        verify(obtainWorkByIdPort, times(1)).execute(workId);
    }
}
