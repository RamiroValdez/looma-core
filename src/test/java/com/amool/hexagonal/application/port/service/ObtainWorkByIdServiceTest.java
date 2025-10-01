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

/**
 * Test unitario para ObtainWorkByIdService
 * Verifica la lógica del caso de uso sin dependencias externas
 */
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
        // Arrange
        Long workId = 1L;
        Work expectedWork = new Work();
        expectedWork.setId(workId);
        expectedWork.setTitle("Test Work");
        
        when(obtainWorkByIdPort.execute(workId)).thenReturn(expectedWork);

        // Act
        Work result = obtainWorkByIdService.execute(workId);

        // Assert
        assertNotNull(result);
        assertEquals(workId, result.getId());
        assertEquals("Test Work", result.getTitle());
        verify(obtainWorkByIdPort, times(1)).execute(workId);
    }

    @Test
    public void testExecute_ShouldReturnNull_WhenWorkDoesNotExist() {
        // Arrange
        Long workId = 999L;
        when(obtainWorkByIdPort.execute(workId)).thenReturn(null);

        // Act
        Work result = obtainWorkByIdService.execute(workId);

        // Assert
        assertNull(result);
        verify(obtainWorkByIdPort, times(1)).execute(workId);
    }
}
