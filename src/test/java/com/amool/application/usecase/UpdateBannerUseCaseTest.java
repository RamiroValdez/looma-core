package com.amool.application.usecase;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.WorkPort;
import com.amool.application.service.ImagesService;
import com.amool.application.usecases.UpdateBannerUseCase;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateBannerUseCaseTest {

    @Mock
    private ObtainWorkByIdPort obtainWorkByIdPort;

    @Mock
    private ImagesService imagesService;

    @Mock
    private WorkPort workPort;

    private UpdateBannerUseCase useCase;
    private final Long workId = 1L;
    private final Long authenticatedUserId = 100L;
    private Work work;
    private MockMultipartFile bannerFile;

    @BeforeEach
    public void setUp() {
        useCase = new UpdateBannerUseCase(obtainWorkByIdPort, imagesService, workPort);
        
        User creator = new User();
        creator.setId(authenticatedUserId);
        
        work = new Work();
        work.setId(workId);
        work.setCreator(creator);
        work.setBanner("old/banner/path.jpg");
        
        bannerFile = new MockMultipartFile(
            "banner", 
            "banner.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );
    }

    @Test
    public void when_ValidRequest_ThenUpdateBanner() throws IOException {
        String newBannerPath = "new/banner/path.jpg";
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));
        when(imagesService.uploadBannerImage(any(), anyString())).thenReturn(newBannerPath);

        useCase.execute(workId, bannerFile, authenticatedUserId);

        verify(imagesService).deleteImage("old/banner/path.jpg");
        verify(imagesService).uploadBannerImage(eq(bannerFile), eq(workId.toString()));
        verify(workPort).updateWork(work);
        assertEquals(newBannerPath, work.getBanner());
    }

    @Test
    public void when_UserNotAuthenticated_ThenThrowSecurityException() {
        assertThrows(SecurityException.class, () -> 
            useCase.execute(workId, bannerFile, null)
        );
        
        verifyNoInteractions(obtainWorkByIdPort, imagesService, workPort);
    }

    @Test
    public void when_WorkNotFound_ThenThrowNoSuchElementException() {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> 
            useCase.execute(workId, bannerFile, authenticatedUserId)
        );
        
        verify(obtainWorkByIdPort).obtainWorkById(workId);
        verifyNoMoreInteractions(obtainWorkByIdPort);
        verifyNoInteractions(imagesService, workPort);
    }


    @Test
    public void when_UploadFails_ThenRethrowException() throws IOException {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));
        when(imagesService.uploadBannerImage(any(), anyString())).thenThrow(new IOException("Upload failed"));

        assertThrows(IOException.class, () -> 
            useCase.execute(workId, bannerFile, authenticatedUserId)
        );

        verify(imagesService).deleteImage("old/banner/path.jpg");
        verify(imagesService).uploadBannerImage(eq(bannerFile), eq(workId.toString()));
        verify(workPort, never()).updateWork(any());
    }
}
