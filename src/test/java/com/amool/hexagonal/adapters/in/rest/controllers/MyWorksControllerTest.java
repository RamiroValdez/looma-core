package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.domain.model.User;
import com.amool.hexagonal.domain.model.Work;
import com.amool.hexagonal.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MyWorksControllerTest {

    @Mock
    private WorkService workService;

    @InjectMocks
    private MyWorksController myWorksController;

    private Work testWork1;
    private Work testWork2;
    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

     
        testWork1 = new Work();
        testWork1.setId(1L);
        testWork1.setTitle("Test Work 1");
        testWork1.setDescription("Description 1");
        testWork1.setState("PUBLISHED");
        testWork1.setPrice(10.99);
        testWork1.setLikes(5);
        testWork1.setPublicationDate(LocalDate.of(2025, 1, 1));
        testWork1.setCreator(testUser);

        testWork2 = new Work();
        testWork2.setId(2L);
        testWork2.setTitle("Test Work 2");
        testWork2.setDescription("Description 2");
        testWork2.setState("DRAFT");
        testWork2.setPrice(15.99);
        testWork2.setLikes(10);
        testWork2.setPublicationDate(LocalDate.of(2025, 2, 1));
        testWork2.setCreator(testUser);
    }

    private JwtUserPrincipal createTestUserPrincipal() {
        return new JwtUserPrincipal(1L, "test@example.com", "Test", "User", "testuser");
    }
    
    private void setupSecurityContext(JwtUserPrincipal principal) {
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getMyWorks_WithAuthenticatedUser_ReturnsWorkResponseDtoList() {
        // Arrange
        JwtUserPrincipal principal = createTestUserPrincipal();
        setupSecurityContext(principal);
        
        List<Work> works = Arrays.asList(testWork1, testWork2);
        when(workService.getAuthenticatedUserWorks(principal.getUserId())).thenReturn(works);

        // Act
        ResponseEntity<List<WorkResponseDto>> response = myWorksController.getMyWorks(principal);
        List<WorkResponseDto> result = response.getBody();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result);
        assertEquals(2, result.size());

        WorkResponseDto dto1 = result.get(0);
        assertEquals(testWork1.getId(), dto1.getId());
        assertEquals(testWork1.getTitle(), dto1.getTitle());
        assertEquals(testWork1.getDescription(), dto1.getDescription());
        assertEquals(testWork1.getState(), dto1.getState());
        assertEquals(testWork1.getPrice(), dto1.getPrice());
        assertEquals(testWork1.getLikes(), dto1.getLikes());

        WorkResponseDto dto2 = result.get(1);
        assertEquals(testWork2.getId(), dto2.getId());
        assertEquals(testWork2.getTitle(), dto2.getTitle());
        assertEquals(testWork2.getDescription(), dto2.getDescription());
        assertEquals(testWork2.getState(), dto2.getState());
        assertEquals(testWork2.getPrice(), dto2.getPrice());
        verify(workService, times(1)).getAuthenticatedUserWorks(principal.getUserId());
    }

    @Test
    void getMyWorks_WithNoWorks_ReturnsEmptyList() {
        // Arrange
        JwtUserPrincipal principal = createTestUserPrincipal();
        setupSecurityContext(principal);
        
        when(workService.getAuthenticatedUserWorks(principal.getUserId())).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<WorkResponseDto>> response = myWorksController.getMyWorks(principal);
        List<WorkResponseDto> result = response.getBody();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(workService, times(1)).getAuthenticatedUserWorks(principal.getUserId());
    }

    @Test
    void getWorksByUserId_WithMatchingUserId_ReturnsUserWorks() {
        // Arrange
        JwtUserPrincipal principal = createTestUserPrincipal();
        setupSecurityContext(principal);
        
        Long userId = principal.getUserId();
        List<Work> works = Arrays.asList(testWork1, testWork2);
        when(workService.getAuthenticatedUserWorks(principal.getUserId())).thenReturn(works);

        // Act
        ResponseEntity<List<WorkResponseDto>> response = myWorksController.getWorksByUserId(userId, principal);
        List<WorkResponseDto> result = response.getBody();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(workService, times(1)).getAuthenticatedUserWorks(principal.getUserId());
    }

    @Test
    void getWorksByUserId_WithMismatchedUserId_ThrowsSecurityException() {
        // Arrange
        JwtUserPrincipal principal = createTestUserPrincipal();
        setupSecurityContext(principal);
        
        Long differentUserId = 999L; // Diferente al ID del principal

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            myWorksController.getWorksByUserId(differentUserId, principal);
        });
        
        verify(workService, never()).getAuthenticatedUserWorks(any());
    }
}
