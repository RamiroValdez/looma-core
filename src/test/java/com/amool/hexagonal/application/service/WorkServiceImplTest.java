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
import com.amool.hexagonal.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WorkServiceImplTest {

    @Mock
    private ObtainWorkByIdPort obtainWorkByIdPort;

    @Mock
    private ImagesService imagesService;

    @Mock
    private DowloadImagesService downloadImagesService;

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

    // --- updateCover tests ---

    @Test
    void updateCover_shouldThrowSecurityException_whenUserNotAuthenticated() {
        MultipartFile file = new MockMultipartFile("cover", "c.jpg", "image/jpeg", new byte[]{1});
        SecurityException ex = assertThrows(SecurityException.class, () ->
                workServiceImpl.updateCover(1L, file, null, null)
        );
        assertEquals("Usuario no autenticado", ex.getMessage());
        verifyNoInteractions(obtainWorkByIdPort);
    }

    @Test
    void updateCover_shouldThrowNoSuchElement_whenWorkNotFound() {
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.empty());
        MultipartFile file = new MockMultipartFile("cover", "c.jpg", "image/jpeg", new byte[]{1});
        assertThrows(java.util.NoSuchElementException.class, () ->
                workServiceImpl.updateCover(1L, file, 10L, null)
        );
        verify(obtainWorkByIdPort).obtainWorkById(1L);
        verifyNoMoreInteractions(workPort);
    }

    @Test
    void updateCover_shouldThrowSecurityException_whenUserIsNotCreator() {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(99L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));

        MultipartFile file = new MockMultipartFile("cover", "c.jpg", "image/jpeg", new byte[]{1});
        assertThrows(SecurityException.class, () ->
                workServiceImpl.updateCover(1L, file, 10L, null)
        );
        verify(obtainWorkByIdPort).obtainWorkById(1L);
        verifyNoMoreInteractions(workPort);
    }

    @Test
    void updateCover_shouldUpdateAndPersist_whenOk() throws Exception {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(10L);
        work.setCreator(creator);
        work.setCover("works/1/cover/old.jpg");
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        when(imagesService.uploadCoverImage(any(), eq("1"))).thenReturn("works/1/cover/cover.jpg");

        MultipartFile file = new MockMultipartFile("cover", "c.jpg", "image/jpeg", new byte[]{1});
        workServiceImpl.updateCover(1L, file, 10L, null);

        InOrder inOrder = inOrder(imagesService, workPort);
        inOrder.verify(imagesService).deleteImage("works/1/cover/old.jpg");
        inOrder.verify(imagesService).uploadCoverImage(file, "1");
        inOrder.verify(workPort).updateWork(any(Work.class));
    }

    @Test
    void updateCover_shouldPropagateIOException_andNotPersistFinalPath_whenUploadFails() throws Exception {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(10L);
        work.setCreator(creator);
        work.setCover("works/1/cover/old.jpg");
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        when(imagesService.uploadCoverImage(any(), anyString())).thenThrow(new IOException("fail"));

        MultipartFile file = new MockMultipartFile("cover", "c.jpg", "image/jpeg", new byte[]{1});

        assertThrows(IOException.class, () -> workServiceImpl.updateCover(1L, file, 10L, null));
        verify(workPort, never()).updateWork(any(Work.class));
        verify(imagesService).deleteImage("works/1/cover/old.jpg");
    }

    @Test
    void updateCover_shouldDownloadFromIaAndPersist_whenNoFileButIaUrlProvided() throws Exception {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(10L);
        work.setCreator(creator);
        work.setCover("works/1/cover/old.jpg");

        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        when(downloadImagesService.downloadAndUploadCoverImage(anyString(), anyString())).thenReturn("works/1/cover/cover.png");

        String iaUrl = "http://archive.org/cover.png";
        workServiceImpl.updateCover(1L, null, 10L, iaUrl);

        InOrder inOrder = inOrder(imagesService, downloadImagesService, workPort);
        inOrder.verify(imagesService).deleteImage("works/1/cover/old.jpg");
        inOrder.verify(downloadImagesService).downloadAndUploadCoverImage(iaUrl, "1");
        inOrder.verify(workPort).updateWork(any(Work.class));
    }


    // --- updateBanner tests ---

    @Test
    void updateBanner_shouldThrowSecurityException_whenUserNotAuthenticated() {
        MultipartFile file = new MockMultipartFile("banner", "b.jpg", "image/jpeg", new byte[]{1});
        SecurityException ex = assertThrows(SecurityException.class, () ->
                workServiceImpl.updateBanner(1L, file, null)
        );
        assertEquals("Usuario no autenticado", ex.getMessage());
        verifyNoInteractions(obtainWorkByIdPort);
    }

    @Test
    void updateBanner_shouldThrowNoSuchElement_whenWorkNotFound() {
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.empty());
        MultipartFile file = new MockMultipartFile("banner", "b.jpg", "image/jpeg", new byte[]{1});
        assertThrows(java.util.NoSuchElementException.class, () ->
                workServiceImpl.updateBanner(1L, file, 10L)
        );
        verify(obtainWorkByIdPort).obtainWorkById(1L);
        verifyNoMoreInteractions(workPort);
    }

    @Test
    void updateBanner_shouldThrowSecurityException_whenUserIsNotCreator() {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(99L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));

        MultipartFile file = new MockMultipartFile("banner", "b.jpg", "image/jpeg", new byte[]{1});
        assertThrows(SecurityException.class, () ->
                workServiceImpl.updateBanner(1L, file, 10L)
        );
        verify(obtainWorkByIdPort).obtainWorkById(1L);
        verifyNoMoreInteractions(workPort);
    }

    @Test
    void updateBanner_shouldUpdateAndPersist_whenOk() throws Exception {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(10L);
        work.setCreator(creator);
        work.setBanner("works/1/banner/old.jpg");
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        when(imagesService.uploadBannerImage(any(), eq("1"))).thenReturn("works/1/banner/banner.jpg");

        MultipartFile file = new MockMultipartFile("banner", "b.jpg", "image/jpeg", new byte[]{1});
        workServiceImpl.updateBanner(1L, file, 10L);

        InOrder inOrder = inOrder(imagesService, workPort);
        inOrder.verify(imagesService).deleteImage("works/1/banner/old.jpg");
        inOrder.verify(imagesService).uploadBannerImage(file, "1");
        inOrder.verify(workPort).updateWork(any(Work.class));
    }

    @Test
    void updateBanner_shouldPropagateIOException_andNotPersistFinalPath_whenUploadFails() throws Exception {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(10L);
        work.setCreator(creator);
        work.setBanner("works/1/banner/old.jpg");
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        when(imagesService.uploadBannerImage(any(), anyString())).thenThrow(new IOException("fail"));

        MultipartFile file = new MockMultipartFile("banner", "b.jpg", "image/jpeg", new byte[]{1});

        assertThrows(IOException.class, () -> workServiceImpl.updateBanner(1L, file, 10L));
        verify(workPort, never()).updateWork(any(Work.class));
        verify(imagesService).deleteImage("works/1/banner/old.jpg");
    }
}
