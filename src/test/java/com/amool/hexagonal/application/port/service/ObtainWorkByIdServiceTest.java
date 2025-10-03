package com.amool.hexagonal.application.port.service;

import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.amool.hexagonal.application.service.ObtainWorkByIdService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

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
        when(obtainWorkByIdPort.execute(workId)).thenReturn(Optional.of(expectedWork));

        Optional<Work> result = obtainWorkByIdService.obtainWorkById(workId);

        assertTrue(result.isPresent());
        assertEquals(workId, result.get().getId());
        assertEquals("Test Work", result.get().getTitle());
        verify(obtainWorkByIdPort, times(1)).execute(workId);
    }

    @Test
    public void testExecute_ShouldReturnNull_WhenWorkDoesNotExist() {
        Long workId = 999L;
        when(obtainWorkByIdPort.execute(workId)).thenReturn(Optional.empty());

        Optional<Work> result = obtainWorkByIdService.obtainWorkById(workId);

        assertTrue(result.isEmpty());
        verify(obtainWorkByIdPort, times(1)).execute(workId);
    }
}
