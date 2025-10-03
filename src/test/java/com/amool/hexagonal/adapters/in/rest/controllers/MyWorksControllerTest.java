package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.domain.model.User;
import com.amool.hexagonal.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void getWorksByUserId_WithValidUserId_ReturnsWorkResponseDtoList() {

        Long userId = 1L;
        List<Work> works = Arrays.asList(testWork1, testWork2);
        when(workService.getWorksByUserId(userId)).thenReturn(works);

        List<WorkResponseDto> result = myWorksController.getWorksByUserId(userId);

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
        assertEquals(testWork2.getLikes(), dto2.getLikes());

        verify(workService, times(1)).getWorksByUserId(userId);
    }

    @Test
    void getWorksByUserId_WithNoWorks_ReturnsEmptyList() {

        Long userId = 1L;
        when(workService.getWorksByUserId(userId)).thenReturn(Collections.emptyList());

        
        List<WorkResponseDto> result = myWorksController.getWorksByUserId(userId);

       
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(workService, times(1)).getWorksByUserId(userId);
    }

    @Test
    void getWorksByUserId_WithSingleWork_ReturnsSingleWorkResponseDto() {
     
        Long userId = 1L;
        List<Work> works = Collections.singletonList(testWork1);
        when(workService.getWorksByUserId(userId)).thenReturn(works);

        
        List<WorkResponseDto> result = myWorksController.getWorksByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWork1.getId(), result.get(0).getId());
        assertEquals(testWork1.getTitle(), result.get(0).getTitle());
        verify(workService, times(1)).getWorksByUserId(userId);
    }

    @Test
    void getWorksByUserId_VerifyMappingIsAppliedToAllWorks() {
      
        Long userId = 1L;
        List<Work> works = Arrays.asList(testWork1, testWork2);
        when(workService.getWorksByUserId(userId)).thenReturn(works);

      
        List<WorkResponseDto> result = myWorksController.getWorksByUserId(userId);

    
        assertEquals(works.size(), result.size());
        for (int i = 0; i < works.size(); i++) {
            Work work = works.get(i);
            WorkResponseDto dto = result.get(i);
            assertEquals(work.getId(), dto.getId());
            assertEquals(work.getTitle(), dto.getTitle());
        }
        verify(workService, times(1)).getWorksByUserId(userId);
    }

    @Test
    void getWorksByUserId_WithDifferentUserIds_CallsUseCaseWithCorrectParameter() {
       
        Long userId1 = 1L;
        Long userId2 = 2L;
        when(workService.getWorksByUserId(anyLong())).thenReturn(Collections.emptyList());

        myWorksController.getWorksByUserId(userId1);
        myWorksController.getWorksByUserId(userId2);

        verify(workService, times(1)).getWorksByUserId(userId1);
        verify(workService, times(1)).getWorksByUserId(userId2);
        verify(workService, times(2)).getWorksByUserId(anyLong());
    }

    @Test
    void getWorksByUserId_WithNullWorkInList_HandlesGracefully() {

        Long userId = 1L;
        List<Work> works = Arrays.asList(testWork1, null, testWork2);
        when(workService.getWorksByUserId(userId)).thenReturn(works);

       
        List<WorkResponseDto> result = myWorksController.getWorksByUserId(userId);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertNotNull(result.get(0));
        assertNull(result.get(1)); 
        assertNotNull(result.get(2));
        verify(workService, times(1)).getWorksByUserId(userId);
    }
}
