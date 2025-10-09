package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.application.port.out.LoadUserPort;
import com.amool.hexagonal.application.port.out.FormatPort;
import com.amool.hexagonal.application.port.out.LoadLanguagePort;
import com.amool.hexagonal.application.port.out.CategoryPort;
import com.amool.hexagonal.application.port.out.WorkPort;
import com.amool.hexagonal.application.port.in.ImagesService;
import com.amool.hexagonal.application.port.in.TagService;
import com.amool.hexagonal.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WorkServiceImplTest {

    @Mock
    private ObtainWorkByIdPort obtainWorkByIdPort;

    @Mock
    private ImagesService imagesService;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private FormatPort formatPort;

    @Mock
    private LoadLanguagePort loadLanguagePort;

    @Mock
    private CategoryPort categoryPort;

    @Mock
    private TagService tagService;

    @Mock
    private WorkPort workPort;

    @InjectMocks
    private WorkServiceImpl workServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(imagesService.getBannerImageUrl(any())).thenAnswer(inv -> inv.getArgument(0));
        when(imagesService.getCoverImageUrl(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    public void testExecute_ShouldReturnWork_WhenWorkExists() {
        Long workId = 1L;
        Work expectedWork = new Work();
        expectedWork.setId(workId);
        expectedWork.setTitle("Test Work");
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(expectedWork));

        Optional<Work> result = workServiceImpl.obtainWorkById(workId);

        assertTrue(result.isPresent());
        assertEquals(workId, result.get().getId());
        assertEquals("Test Work", result.get().getTitle());
        verify(obtainWorkByIdPort, times(1)).obtainWorkById(workId);
    }

    @Test
    public void testExecute_ShouldReturnNull_WhenWorkDoesNotExist() {
        Long workId = 999L;
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.empty());

        Optional<Work> result = workServiceImpl.obtainWorkById(workId);

        assertTrue(result.isEmpty());
        verify(obtainWorkByIdPort, times(1)).obtainWorkById(workId);
    }
    
    @Test
    public void testGetAuthenticatedUserWorks_ShouldReturnUserWorks_WhenUserIsAuthenticated() {
        Long userId = 1L;
        Work work1 = new Work();
        work1.setId(1L);
        work1.setTitle("Work 1");
        
        Work work2 = new Work();
        work2.setId(2L);
        work2.setTitle("Work 2");
        
        List<Work> expectedWorks = Arrays.asList(work1, work2);
        when(obtainWorkByIdPort.getWorksByUserId(userId)).thenReturn(expectedWorks);
        
        List<Work> result = workServiceImpl.getAuthenticatedUserWorks(userId);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Work 1", result.get(0).getTitle());
        assertEquals("Work 2", result.get(1).getTitle());
        verify(obtainWorkByIdPort, times(1)).getWorksByUserId(userId);
    }
    
    @Test
    public void testGetAuthenticatedUserWorks_ShouldThrowSecurityException_WhenUserIdIsNull() {
        Long userId = null;
        
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            workServiceImpl.getAuthenticatedUserWorks(userId);
        });
        
        assertEquals("Usuario no autenticado", exception.getMessage());
        verify(obtainWorkByIdPort, never()).getWorksByUserId(any());
    }
    
    @Test
    public void testGetWorksByUserId_ShouldReturnWorksForUser() {
        Long userId = 1L;
        Work work1 = new Work();
        work1.setId(1L);
        work1.setTitle("Work 1");
        
        List<Work> expectedWorks = Arrays.asList(work1);
        when(obtainWorkByIdPort.getWorksByUserId(userId)).thenReturn(expectedWorks);
        
        List<Work> result = workServiceImpl.getWorksByUserId(userId);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Work 1", result.get(0).getTitle());
        verify(obtainWorkByIdPort, times(1)).getWorksByUserId(userId);
    }
}
