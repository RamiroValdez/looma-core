package com.amool.application.usecase;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.WorkPort;
import com.amool.application.service.ImagesService;
import com.amool.application.usecases.UpdateCover;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class UpdateCoverTest {

    private static final Long WORK_ID = 1L;
    private static final Long OWNER_ID = 100L;
    private static final Long OTHER_USER_ID = 200L;
    private static final String OLD_COVER_PATH = "works/1/cover/old-cover.jpg";
    private static final String NEW_COVER_PATH = "works/1/cover/new-cover.jpg";
    private static final String AI_URL = "https://example.com/ai-cover.jpg";
    private static final String AI_COVER_PATH = "works/1/cover/ai-cover.jpg";

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private ImagesService imagesService;
    private WorkPort workPort;
    private UpdateCover useCase;
    private Work work;
    private MultipartFile coverFile;

    @BeforeEach
    public void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        imagesService = Mockito.mock(ImagesService.class);
        workPort = Mockito.mock(WorkPort.class);
        useCase = new UpdateCover(obtainWorkByIdPort, imagesService, workPort);
        work = buildWorkWithOwner(OWNER_ID);
        coverFile = buildCoverFile();
    }

    @Test
    public void shouldUpdateCoverUsingUploadedFile() throws Exception {
        givenWorkExistsOwnedBy(OWNER_ID);
        givenCoverUploadSucceeds(NEW_COVER_PATH);

        whenUpdatingCoverWithFile(OWNER_ID);

        thenOldCoverDeleted();
        thenCoverUploadedFromFile();
        thenWorkUpdated();
        thenCoverPathIs(NEW_COVER_PATH);
    }

    @Test
    public void shouldUpdateCoverUsingAiUrl() throws Exception {
        givenWorkExistsOwnedBy(OWNER_ID);
        givenAiCoverUploadSucceeds(AI_URL, AI_COVER_PATH);

        whenUpdatingCoverWithAiUrl(OWNER_ID, AI_URL);

        thenOldCoverDeleted();
        thenCoverDownloadedFromAi(AI_URL);
        thenWorkUpdated();
        thenCoverPathIs(AI_COVER_PATH);
    }

    @Test
    public void shouldThrowSecurityExceptionWhenUserNotAuthenticated() {
        thenThrows(SecurityException.class, updatingCover(null, null, null));
        thenNoPortsInvoked();
    }

    @Test
    public void shouldThrowNoSuchElementWhenWorkIsMissing() {
        givenWorkDoesNotExist();

        thenThrows(NoSuchElementException.class, updatingCover(OWNER_ID, null, null));
        thenWorkLookupOccurs();
        thenNoCoverUploadOccurs();
        thenNoWorkUpdate();
    }

    @Test
    public void shouldThrowSecurityExceptionWhenUserNotAuthorized() {
        givenWorkExistsOwnedBy(OWNER_ID);

        thenThrows(SecurityException.class, updatingCover(OTHER_USER_ID, null, null));
        thenWorkLookupOccurs();
        thenNoCoverUploadOccurs();
        thenNoWorkUpdate();
    }

    private Work buildWorkWithOwner(Long ownerId) {
        User creator = new User();
        creator.setId(ownerId);
        Work entity = new Work();
        entity.setId(WORK_ID);
        entity.setCover(OLD_COVER_PATH);
        entity.setCreator(creator);
        return entity;
    }

    private MultipartFile buildCoverFile() {
        return new MockMultipartFile(
            "cover",
            "cover.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
    }

    private void givenWorkExistsOwnedBy(Long ownerId) {
        work.setCreator(buildUser(ownerId));
        Mockito.when(obtainWorkByIdPort.obtainWorkById(WORK_ID)).thenReturn(Optional.of(work));
    }

    private void givenWorkDoesNotExist() {
        Mockito.when(obtainWorkByIdPort.obtainWorkById(WORK_ID)).thenReturn(Optional.empty());
    }

    private void givenCoverUploadSucceeds(String newCoverPath) throws Exception {
        Mockito.when(imagesService.uploadCoverImage(eq(coverFile), eq(WORK_ID.toString()))).thenReturn(newCoverPath);
    }

    private void givenAiCoverUploadSucceeds(String aiUrl, String newCoverPath) throws Exception {
        Mockito.when(imagesService.downloadAndUploadCoverImage(eq(aiUrl), eq(WORK_ID.toString()))).thenReturn(newCoverPath);
    }

    private void whenUpdatingCoverWithFile(Long userId) throws Exception {
        useCase.execute(WORK_ID, coverFile, userId, null);
    }

    private void whenUpdatingCoverWithAiUrl(Long userId, String aiUrl) throws Exception {
        useCase.execute(WORK_ID, null, userId, aiUrl);
    }

    private Executable updatingCover(Long userId, MultipartFile file, String aiUrl) {
        return () -> useCase.execute(WORK_ID, file, userId, aiUrl);
    }

    private void thenOldCoverDeleted() {
        verify(imagesService).deleteImage(OLD_COVER_PATH);
    }

    private void thenCoverUploadedFromFile() throws Exception {
        verify(imagesService).uploadCoverImage(eq(coverFile), eq(WORK_ID.toString()));
    }

    private void thenCoverDownloadedFromAi(String aiUrl) throws Exception {
        verify(imagesService).downloadAndUploadCoverImage(eq(aiUrl), eq(WORK_ID.toString()));
    }

    private void thenWorkUpdated() {
        verify(workPort).updateWork(work);
    }

    private void thenCoverPathIs(String expectedPath) {
        assertEquals(expectedPath, work.getCover());
    }

    private void thenThrows(Class<? extends Throwable> expected, Executable executable) {
        assertThrows(expected, executable);
    }

    private void thenNoPortsInvoked() {
        Mockito.verifyNoInteractions(obtainWorkByIdPort, imagesService, workPort);
    }

    private void thenWorkLookupOccurs() {
        verify(obtainWorkByIdPort).obtainWorkById(WORK_ID);
    }

    private void thenNoCoverUploadOccurs() {
        assertDoesNotThrow(() -> verify(imagesService, never()).uploadCoverImage(any(), anyString()));
        assertDoesNotThrow(() -> verify(imagesService, never()).downloadAndUploadCoverImage(anyString(), anyString()));
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
