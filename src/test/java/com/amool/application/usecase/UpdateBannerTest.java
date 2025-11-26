package com.amool.application.usecase;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.WorkPort;
import com.amool.application.service.ImagesService;
import com.amool.application.usecases.UpdateBanner;
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
import org.junit.jupiter.api.function.Executable;

@ExtendWith(MockitoExtension.class)
public class UpdateBannerTest {

    private static final Long WORK_ID = 1L;
    private static final Long AUTHENTICATED_USER_ID = 100L;
    private static final String OLD_BANNER_PATH = "old/banner/path.jpg";
    private static final String NEW_BANNER_PATH = "new/banner/path.jpg";

    @Mock
    private ObtainWorkByIdPort obtainWorkByIdPort;

    @Mock
    private ImagesService imagesService;

    @Mock
    private WorkPort workPort;

    private UpdateBanner useCase;
    private Work work;
    private MockMultipartFile bannerFile;

    @BeforeEach
    public void setUp() {
        useCase = new UpdateBanner(obtainWorkByIdPort, imagesService, workPort);
        work = buildWorkWithOwner(AUTHENTICATED_USER_ID);
        bannerFile = buildBannerFile();
    }

    @Test
    public void shouldUpdateBannerWhenRequestIsValid() throws IOException {
        givenWorkExistsOwnedBy(AUTHENTICATED_USER_ID);
        givenBannerUploadSucceeds(NEW_BANNER_PATH);

        whenUpdatingBanner(AUTHENTICATED_USER_ID);

        thenOldBannerDeleted();
        thenBannerUploadedWith(bannerFile, WORK_ID.toString());
        thenWorkUpdated();
        thenBannerPathIs(NEW_BANNER_PATH);
    }

    @Test
    public void shouldThrowSecurityExceptionWhenUserNotAuthenticated() {
        thenThrows(SecurityException.class, () -> whenUpdatingBanner(null));
        thenNoPortsInvoked();
    }

    @Test
    public void shouldThrowNoSuchElementWhenWorkIsMissing() {
        givenWorkDoesNotExist();

        thenThrows(NoSuchElementException.class, () -> whenUpdatingBanner(AUTHENTICATED_USER_ID));
        thenWorkLookupOccurs();
        thenNoBannerUpload();
        thenNoWorkUpdate();
    }

    @Test
    public void shouldPropagateExceptionWhenUploadFails() throws IOException {
        givenWorkExistsOwnedBy(AUTHENTICATED_USER_ID);
        givenBannerUploadFails(new IOException("Upload failed"));

        thenThrows(IOException.class, () -> whenUpdatingBanner(AUTHENTICATED_USER_ID));
        thenOldBannerDeleted();
        thenBannerUploadedWith(bannerFile, WORK_ID.toString());
        thenNoWorkUpdate();
    }

    private Work buildWorkWithOwner(Long ownerId) {
        User creator = new User();
        creator.setId(ownerId);
        Work builtWork = new Work();
        builtWork.setId(WORK_ID);
        builtWork.setCreator(creator);
        builtWork.setBanner(OLD_BANNER_PATH);
        return builtWork;
    }

    private MockMultipartFile buildBannerFile() {
        return new MockMultipartFile(
            "banner",
            "banner.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
    }

    private void givenWorkExistsOwnedBy(Long ownerId) {
        work.setCreator(buildUser(ownerId));
        when(obtainWorkByIdPort.obtainWorkById(WORK_ID)).thenReturn(Optional.of(work));
    }

    private void givenWorkDoesNotExist() {
        when(obtainWorkByIdPort.obtainWorkById(WORK_ID)).thenReturn(Optional.empty());
    }

    private void givenBannerUploadSucceeds(String newPath) throws IOException {
        when(imagesService.uploadBannerImage(any(), anyString())).thenReturn(newPath);
    }

    private void givenBannerUploadFails(IOException exception) throws IOException {
        when(imagesService.uploadBannerImage(any(), anyString())).thenThrow(exception);
    }

    private void whenUpdatingBanner(Long userId) throws IOException {
        useCase.execute(WORK_ID, bannerFile, userId);
    }

    private void thenOldBannerDeleted() {
        verify(imagesService).deleteImage(OLD_BANNER_PATH);
    }

    private void thenBannerUploadedWith(MockMultipartFile file, String workIdAsString) throws IOException {
        verify(imagesService).uploadBannerImage(eq(file), eq(workIdAsString));
    }

    private void thenWorkUpdated() {
        verify(workPort).updateWork(work);
    }

    private void thenBannerPathIs(String expectedPath) {
        assertEquals(expectedPath, work.getBanner());
    }

    private void thenThrows(Class<? extends Throwable> expected, Executable action) {
        assertThrows(expected, action);
    }

    private void thenNoPortsInvoked() {
        verifyNoInteractions(obtainWorkByIdPort, imagesService, workPort);
    }

    private void thenWorkLookupOccurs() {
        verify(obtainWorkByIdPort).obtainWorkById(WORK_ID);
    }

    private void thenNoBannerUpload() {
        assertDoesNotThrow(() -> verify(imagesService, never()).uploadBannerImage(any(), anyString()));
        verify(imagesService, never()).deleteImage(anyString());
    }

    private void thenNoWorkUpdate() {
        verify(workPort, never()).updateWork(any());
    }

    private User buildUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}
