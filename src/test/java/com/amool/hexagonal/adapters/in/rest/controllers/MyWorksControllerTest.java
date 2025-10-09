package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.domain.model.Work;
import com.amool.hexagonal.security.JwtUserPrincipal;
import com.amool.hexagonal.domain.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyWorksControllerTest {

    @Mock
    private WorkService workService;

    @InjectMocks
    private MyWorksController myWorksController;

    private JwtUserPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testPrincipal = mock(JwtUserPrincipal.class);
        when(testPrincipal.getUserId()).thenReturn(1L);
    }

    @Test
    void getWorksByUserId_WithMatchingUserId_ReturnsUserWorks() {
        Long userId = 1L;
        Work work1 = new Work();
        work1.setId(1L);
        work1.setTitle("Trabajo 1");
        work1.setDescription("Descripción 1");

        Work work2 = new Work();
        work2.setId(2L);
        work2.setTitle("Trabajo 2");
        work2.setDescription("Descripción 2");

        when(workService.getAuthenticatedUserWorks(1L)).thenReturn(Arrays.asList(work1, work2));

        ResponseEntity<List<WorkResponseDto>> response =
                myWorksController.getWorksByUserId(userId, testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(workService).getAuthenticatedUserWorks(1L);
    }

    @Test
    void getWorksByUserId_WithEmptyResults_ReturnsEmptyArray() {
        Long userId = 1L;
        when(workService.getAuthenticatedUserWorks(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<WorkResponseDto>> response =
                myWorksController.getWorksByUserId(userId, testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(workService).getAuthenticatedUserWorks(1L);
    }

    @Test
    void getWorksByUserId_WithServiceException_ThrowsUnauthorizedAccessException() {
        Long userId = 1L;
        when(workService.getAuthenticatedUserWorks(1L))
                .thenThrow(new RuntimeException("Database error"));
        assertThrows(UnauthorizedAccessException.class, () -> {
            myWorksController.getWorksByUserId(userId, testPrincipal);
        });

        verify(workService).getAuthenticatedUserWorks(1L);
    }
}
