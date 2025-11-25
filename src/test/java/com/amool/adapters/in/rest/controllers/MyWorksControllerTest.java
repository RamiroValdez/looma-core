package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.application.usecases.GetAuthenticatedUserWorks;
import com.amool.application.usecases.UpdateBanner;
import com.amool.application.usecases.UpdateCover;
import com.amool.domain.exception.UnauthorizedAccessException;
import com.amool.domain.model.Work;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MyWorksControllerTest {

    private MyWorksController controller;
    private GetAuthenticatedUserWorks getAuthenticatedUserWorks;
    private UpdateCover updateCover;
    private UpdateBanner updateBanner;

    private static final Long USER_ID = 77L;

    @BeforeEach
    void setUp() {
        getAuthenticatedUserWorks = Mockito.mock(GetAuthenticatedUserWorks.class);
        updateCover = Mockito.mock(UpdateCover.class);
        updateBanner = Mockito.mock(UpdateBanner.class);
        controller = new MyWorksController(getAuthenticatedUserWorks, updateCover, updateBanner);
    }


    @Test
    @DisplayName("GET /api/my-works/{userId} - Debe devolver lista mapeada cuando el usuario está autorizado")
    void getWorksByUserId_shouldReturnMappedList_whenAuthorized() {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        List<Work> works = givenWorks(
                givenWork(1L, "Obra A"),
                givenWork(2L, "Obra B")
        );
        givenUseCaseReturnsWorks(works);

        ResponseEntity<List<WorkResponseDto>> response = whenGettingWorksByUserId(USER_ID, principal);

        thenShouldReturnOk(response);
        thenListHasTitles(response.getBody(), "Obra A", "Obra B");
        thenGetWorksUseCaseWasCalledWith(USER_ID);
    }

    @Test
    @DisplayName("GET /api/my-works/{userId} - Debe lanzar UnauthorizedAccessException cuando el caso de uso falla")
    void getWorksByUserId_shouldThrowUnauthorized_whenUseCaseThrows() {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        givenUseCaseThrows(new RuntimeException("internal error"));

        UnauthorizedAccessException ex = assertThrows(UnauthorizedAccessException.class,
                () -> whenGettingWorksByUserId(USER_ID, principal));
        assertTrue(ex.getMessage().contains("No se pudo obtener las obras del usuario autenticado"));
    }


    @Test
    @DisplayName("PATCH /api/my-works/{workId}/cover - Debe devolver 204 cuando actualiza correctamente")
    void updateCover_shouldReturnNoContent_onSuccess() throws Exception {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        MultipartFile file = givenCoverFile();
        String coverIaUrl = "https://cdn.example.com/cover.png";

        ResponseEntity<Void> response = whenUpdatingCover(10L, coverIaUrl, file, principal);

        thenShouldReturnNoContent(response);
        thenUpdateCoverWasCalled(10L, file, USER_ID, coverIaUrl);
    }

    @Test
    @DisplayName("PATCH /api/my-works/{workId}/cover - Debe devolver 404 cuando IllegalArgumentException")
    void updateCover_shouldReturnNotFound_onIllegalArgument() throws Exception {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        MultipartFile file = givenCoverFile();
        doThrow(new IllegalArgumentException("not found")).when(updateCover)
                .execute(eq(10L), any(), eq(USER_ID), any());

        ResponseEntity<Void> response = whenUpdatingCover(10L, null, file, principal);

        thenShouldReturnNotFound(response);
    }

    @Test
    @DisplayName("PATCH /api/my-works/{workId}/cover - Debe devolver 403 cuando SecurityException")
    void updateCover_shouldReturnForbidden_onSecurityException() throws Exception {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        MultipartFile file = givenCoverFile();
        doThrow(new SecurityException("forbidden")).when(updateCover)
                .execute(eq(10L), any(), eq(USER_ID), any());

        ResponseEntity<Void> response = whenUpdatingCover(10L, null, file, principal);

        thenShouldReturnForbidden(response);
    }

    @Test
    @DisplayName("PATCH /api/my-works/{workId}/cover - Debe devolver 400 cuando IOException")
    void updateCover_shouldReturnBadRequest_onIOException() throws Exception {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        MultipartFile file = givenCoverFile();
        doThrow(new IOException("io error")).when(updateCover)
                .execute(eq(10L), any(), eq(USER_ID), any());

        ResponseEntity<Void> response = whenUpdatingCover(10L, null, file, principal);

        thenShouldReturnBadRequest(response);
    }

    @Test
    @DisplayName("PATCH /api/my-works/{workId}/cover - Debe devolver 500 cuando InterruptedException y marcar el hilo")
    void updateCover_shouldReturn500_onInterruptedException() throws Exception {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        MultipartFile file = givenCoverFile();
        doThrow(new InterruptedException("interrupted")).when(updateCover)
                .execute(eq(10L), any(), eq(USER_ID), any());

        boolean wasInterruptedBefore = Thread.currentThread().isInterrupted();
        try {
            ResponseEntity<Void> response = whenUpdatingCover(10L, null, file, principal);

            thenShouldReturnStatus(response, 500);
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
            assertFalse(wasInterruptedBefore && Thread.currentThread().isInterrupted());
        }
    }


    @Test
    @DisplayName("PATCH /api/my-works/{workId}/banner - Debe devolver 204 cuando actualiza correctamente")
    void updateBanner_shouldReturnNoContent_onSuccess() throws Exception {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        MultipartFile file = givenBannerFile();

        ResponseEntity<Void> response = whenUpdatingBanner(10L, file, principal);

        thenShouldReturnNoContent(response);
        thenUpdateBannerWasCalled(10L, file, USER_ID);
    }

    @Test
    @DisplayName("PATCH /api/my-works/{workId}/banner - Debe devolver 404 cuando IllegalArgumentException")
    void updateBanner_shouldReturnNotFound_onIllegalArgument() throws Exception {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        MultipartFile file = givenBannerFile();
        doThrow(new IllegalArgumentException("not found")).when(updateBanner)
                .execute(eq(10L), any(), eq(USER_ID));

        ResponseEntity<Void> response = whenUpdatingBanner(10L, file, principal);

        thenShouldReturnNotFound(response);
    }

    @Test
    @DisplayName("PATCH /api/my-works/{workId}/banner - Debe devolver 403 cuando SecurityException")
    void updateBanner_shouldReturnForbidden_onSecurityException() throws Exception {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        MultipartFile file = givenBannerFile();
        doThrow(new SecurityException("forbidden")).when(updateBanner)
                .execute(eq(10L), any(), eq(USER_ID));

        ResponseEntity<Void> response = whenUpdatingBanner(10L, file, principal);

        thenShouldReturnForbidden(response);
    }

    @Test
    @DisplayName("PATCH /api/my-works/{workId}/banner - Debe devolver 400 cuando IOException")
    void updateBanner_shouldReturnBadRequest_onIOException() throws Exception {
        JwtUserPrincipal principal = givenPrincipal(USER_ID);
        MultipartFile file = givenBannerFile();
        doThrow(new IOException("io error")).when(updateBanner)
                .execute(eq(10L), any(), eq(USER_ID));

        ResponseEntity<Void> response = whenUpdatingBanner(10L, file, principal);

        thenShouldReturnBadRequest(response);
    }

    private JwtUserPrincipal givenPrincipal(Long userId) {
        return new JwtUserPrincipal(userId, "user@example.com", "Name", "Surname", "username");
    }

    private Work givenWork(Long id, String title) {
        Work w = new Work();
        w.setId(id);
        w.setTitle(title);
        return w;
    }

    private List<Work> givenWorks(Work... works) {
        return List.of(works);
    }

    private void givenUseCaseReturnsWorks(List<Work> works) {
        when(getAuthenticatedUserWorks.execute(eq(USER_ID))).thenReturn(works);
    }

    private void givenUseCaseThrows(RuntimeException ex) {
        when(getAuthenticatedUserWorks.execute(eq(USER_ID))).thenThrow(ex);
    }

    private MultipartFile givenCoverFile() {
        return new MockMultipartFile("cover", "cover.jpg", "image/jpeg", "bytes".getBytes());
    }

    private MultipartFile givenBannerFile() {
        return new MockMultipartFile("banner", "banner.jpg", "image/jpeg", "bytes".getBytes());
    }

    private ResponseEntity<List<WorkResponseDto>> whenGettingWorksByUserId(Long userId, JwtUserPrincipal principal) {
        return controller.getWorksByUserId(userId, principal);
    }

    private ResponseEntity<Void> whenUpdatingCover(Long workId, String coverIaUrl, MultipartFile file, JwtUserPrincipal principal) {
        return controller.updateCover(workId, coverIaUrl, file, principal);
    }

    private ResponseEntity<Void> whenUpdatingBanner(Long workId, MultipartFile file, JwtUserPrincipal principal) {
        return controller.updateBanner(workId, file, principal);
    }

    private void thenShouldReturnOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenListHasTitles(List<WorkResponseDto> list, String... titles) {
        assertNotNull(list);
        assertEquals(titles.length, list.size());
        for (int i = 0; i < titles.length; i++) {
            assertEquals(titles[i], list.get(i).getTitle());
        }
    }

    private void thenGetWorksUseCaseWasCalledWith(Long userId) {
        verify(getAuthenticatedUserWorks, times(1)).execute(eq(userId));
    }

    private void thenShouldReturnNoContent(ResponseEntity<Void> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    private void thenShouldReturnNotFound(ResponseEntity<Void> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private void thenShouldReturnForbidden(ResponseEntity<Void> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private void thenShouldReturnBadRequest(ResponseEntity<Void> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private void thenShouldReturnStatus(ResponseEntity<Void> response, int status) {
        assertNotNull(response);
        assertEquals(status, response.getStatusCode().value());
    }

    private void thenUpdateCoverWasCalled(Long workId, MultipartFile file, Long userId, String iaUrl) throws Exception {
        verify(updateCover, times(1)).execute(eq(workId), eq(file), eq(userId), eq(iaUrl));
    }

    private void thenUpdateBannerWasCalled(Long workId, MultipartFile file, Long userId) throws Exception {
        verify(updateBanner, times(1)).execute(eq(workId), eq(file), eq(userId));
    }
}
