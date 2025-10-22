package com.amool.application.usecase;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.WorkPort;
import com.amool.application.service.ImagesService;
import com.amool.application.usecases.UpdateCoverUseCase;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

public class UpdateCoverUseCaseTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private ImagesService imagesService;
    private WorkPort workPort;
    private UpdateCoverUseCase useCase;

    @BeforeEach
    public void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        imagesService = Mockito.mock(ImagesService.class);
        workPort = Mockito.mock(WorkPort.class);
        
        useCase = new UpdateCoverUseCase(
            obtainWorkByIdPort,
            imagesService,
            workPort
        );
    }

    @Test
    public void when_UpdateCoverWithFile_ThenUpdateSuccessfully() throws Exception {
        Long workId = 1L;
        Long userId = 100L;
        String newCoverPath = "works/1/cover/new-cover.jpg";
        
        Work work = new Work();
        work.setId(workId);
        work.setCover("works/1/cover/old-cover.jpg");
        
        User creator = new User();
        creator.setId(userId);
        work.setCreator(creator);
        
        MultipartFile coverFile = new MockMultipartFile(
            "cover", 
            "cover.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
            .thenReturn(Optional.of(work));
        
        Mockito.when(imagesService.uploadCoverImage(any(MultipartFile.class), anyString()))
            .thenReturn(newCoverPath);

        useCase.execute(workId, coverFile, userId, null);

        String oldCoverPath = "works/1/cover/old-cover.jpg";
        Mockito.verify(imagesService).deleteImage(oldCoverPath);
        
        Mockito.verify(imagesService).uploadCoverImage(
            any(MultipartFile.class),
            eq(workId.toString())
        );
        
        Mockito.verify(workPort).updateWork(work);
        
        assertEquals(newCoverPath, work.getCover());
    }

    @Test
    public void when_UpdateCoverWithAiUrl_ThenUpdateSuccessfully() throws Exception {
        Long workId = 1L;
        Long userId = 100L;
        String aiUrl = "https://example.com/ai-cover.jpg";
        String newCoverPath = "works/1/cover/ai-cover.jpg";
        
        Work work = new Work();
        work.setId(workId);
        work.setCover("works/1/cover/old-cover.jpg");
        
        User creator = new User();
        creator.setId(userId);
        work.setCreator(creator);

        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
            .thenReturn(Optional.of(work));
        
        Mockito.when(imagesService.downloadAndUploadCoverImage(aiUrl, workId.toString()))
            .thenReturn(newCoverPath);

        useCase.execute(workId, null, userId, aiUrl);

        String oldCoverPath = "works/1/cover/old-cover.jpg";
        Mockito.verify(imagesService).deleteImage(oldCoverPath);
        
        Mockito.verify(imagesService).downloadAndUploadCoverImage(
            eq(aiUrl),
            eq(workId.toString())
        );
        
        Mockito.verify(workPort).updateWork(work);
        
        assertEquals(newCoverPath, work.getCover());
    }

    @Test
    public void when_UserNotAuthenticated_ThenThrowSecurityException() {
        Long workId = 1L;
        
        Exception exception = assertThrows(SecurityException.class, () ->
            useCase.execute(workId, null, null, null)
        );
        
        assertEquals("Usuario no autenticado", exception.getMessage());
        Mockito.verifyNoInteractions(obtainWorkByIdPort, imagesService, workPort);
    }

    @Test
    public void when_WorkNotFound_ThenThrowNoSuchElementException() {
        Long workId = 1L;
        Long userId = 100L;
        
        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
            .thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () ->
            useCase.execute(workId, null, userId, null)
        );

        assertEquals("Obra no encontrada", exception.getMessage());
        Mockito.verify(obtainWorkByIdPort).obtainWorkById(workId);
        Mockito.verifyNoInteractions(imagesService, workPort);
    }

    @Test
    public void when_UserNotAuthorized_ThenThrowSecurityException() {
        Long workId = 1L;
        Long ownerId = 100L;
        Long otherUserId = 200L;
        
        Work work = new Work();
        work.setId(workId);
        
        User creator = new User();
        creator.setId(ownerId);
        work.setCreator(creator);

        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
            .thenReturn(Optional.of(work));

        Exception exception = assertThrows(SecurityException.class, () ->
            useCase.execute(workId, null, otherUserId, null)
        );

        assertEquals("No autorizado para modificar esta obra", exception.getMessage());
        Mockito.verify(obtainWorkByIdPort).obtainWorkById(workId);
        Mockito.verifyNoInteractions(imagesService, workPort);
    }


}
