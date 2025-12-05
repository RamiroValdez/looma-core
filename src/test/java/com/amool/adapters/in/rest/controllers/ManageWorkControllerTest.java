package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.CreateEmptyChapterRequest;
import com.amool.adapters.in.rest.dtos.CreateEmptyChapterResponse;
import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.dtos.UpdateWorkDto;
import com.amool.application.usecases.CreateEmptyChapter;
import com.amool.application.usecases.GetWorkPermissions;
import com.amool.application.usecases.ObtainWorkById;
import com.amool.application.usecases.UpdateWork;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import com.amool.domain.model.WorkPermissions;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ManageWorkControllerTest {

    private ManageWorkController controller;
    private ObtainWorkById obtainWorkById;
    private CreateEmptyChapter createEmptyChapter;
    private GetWorkPermissions getWorkPermissions;
    private UpdateWork updateWork;

    private static final Long USER_ID = 10L;
    private static final Long WORK_ID = 100L;

    @BeforeEach
    void setUp() {
        obtainWorkById = Mockito.mock(ObtainWorkById.class);
        createEmptyChapter = Mockito.mock(CreateEmptyChapter.class);
        getWorkPermissions = Mockito.mock(GetWorkPermissions.class);
        updateWork = Mockito.mock(UpdateWork.class);
        controller = new ManageWorkController(obtainWorkById, createEmptyChapter, getWorkPermissions, updateWork);
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("GET /api/manage-work/{workId} - Debe devolver WorkResponseDto con permisos cuando existe y autenticado")
    void getWorkById_shouldReturnDtoWithPermissions_whenExistsAndAuthenticated() {
        JwtUserPrincipal principal = givenAuthenticatedUser(USER_ID);
        Work work = givenWork(WORK_ID, "Mi Obra", 20L);
        givenWorkFound(work, USER_ID);
        givenPermissionsForUser(work, USER_ID, true, false, List.of(1L, 2L, 3L));

        ResponseEntity<WorkResponseDto> response = whenGettingWorkById(WORK_ID, principal);

        thenShouldReturnOk(response);
        thenWorkIdIs(response.getBody(), WORK_ID);
        thenWorkTitleIs(response.getBody(), "Mi Obra");
        thenPermissionsAre(response.getBody(), true, false, List.of(1L, 2L, 3L));
        thenUseCasesWereCalledForGet(work, USER_ID);
    }

    @Test
    @DisplayName("GET /api/manage-work/{workId} - Debe devolver 404 cuando no existe")
    void getWorkById_shouldReturn404_whenNotFound() {
        JwtUserPrincipal principal = givenAuthenticatedUser(USER_ID);
        givenWorkNotFound(USER_ID);

        ResponseEntity<WorkResponseDto> response = whenGettingWorkById(WORK_ID, principal);

        thenShouldReturnNotFound(response);
        thenPermissionsUseCaseNotCalled();
    }


    @Test
    @DisplayName("POST /api/manage-work/create-chapter - Debe devolver 200 con chapterId")
    void createEmptyChapter_shouldReturnChapterId_onSuccess() {
        CreateEmptyChapterRequest request = givenCreateEmptyChapterRequest(WORK_ID, 1L, "TEXT");
        givenCreateEmptyChapterSucceeds(99L);

        ResponseEntity<CreateEmptyChapterResponse> response = whenCreatingEmptyChapter(request);

        thenShouldReturnOk(response);
        thenChapterIdIs(response.getBody(), 99L);
    }

    @Test
    @DisplayName("POST /api/manage-work/create-chapter - Debe devolver 400 cuando use case lanza excepción")
    void createEmptyChapter_shouldReturnBadRequest_onException() {
        CreateEmptyChapterRequest request = givenCreateEmptyChapterRequest(WORK_ID, 1L, "TEXT");
        givenCreateEmptyChapterFails(new RuntimeException("failure"));

        ResponseEntity<CreateEmptyChapterResponse> response = whenCreatingEmptyChapter(request);

        thenShouldReturnBadRequest(response);
    }


    @Test
    @DisplayName("PUT /api/manage-work/{workId} - Debe devolver 200 (true) cuando el use case actualiza")
    void updateWork_shouldReturnTrue_onSuccess() {
        JwtUserPrincipal principal = givenAuthenticatedUser(USER_ID);
        UpdateWorkDto request = new UpdateWorkDto(new BigDecimal("9.99"), "PUBLISHED", Set.of("t1","t2"), Set.of());
        when(updateWork.execute(eq(WORK_ID), eq(new BigDecimal("9.99")), eq(Set.of("t1","t2")), eq(Set.of()), eq("PUBLISHED")))
                .thenReturn(true);

        ResponseEntity<Boolean> response = controller.updateWork(WORK_ID, request, principal);

        thenShouldReturnOk(response);
        assertTrue(Boolean.TRUE.equals(response.getBody()));
        verify(updateWork, times(1)).execute(eq(WORK_ID), eq(new BigDecimal("9.99")), eq(Set.of("t1","t2")), eq(Set.of()), eq("PUBLISHED"));
    }

    @Test
    @DisplayName("PUT /api/manage-work/{workId} - Debe devolver 400 cuando el use case lanza excepción")
    void updateWork_shouldReturnBadRequest_onException() {
        JwtUserPrincipal principal = givenAuthenticatedUser(USER_ID);
        UpdateWorkDto request = new UpdateWorkDto(new BigDecimal("5.00"), "DRAFT", Set.of(), Set.of());
        when(updateWork.execute(anyLong(), any(), anySet(), anySet(), anyString())).thenThrow(new RuntimeException("error"));

        ResponseEntity<Boolean> response = controller.updateWork(WORK_ID, request, principal);

        thenShouldReturnBadRequest(response);
    }

    private JwtUserPrincipal givenAuthenticatedUser(Long userId) {
        JwtUserPrincipal principal = new JwtUserPrincipal(userId, "u@e.com", "Name", "Surname", "user");
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return principal;
    }

    private Work givenWork(Long id, String title, Long creatorId) {
        Work w = new Work();
        w.setId(id);
        w.setTitle(title);
        if (creatorId != null) {
            User creator = new User();
            creator.setId(creatorId);
            w.setCreator(creator);
        }
        return w;
    }

    private void givenWorkFound(Work work, Long userId) {
        when(obtainWorkById.execute(eq(work.getId()), eq(userId))).thenReturn(Optional.of(work));
    }

    private void givenWorkNotFound(Long userId) {
        when(obtainWorkById.execute(eq(WORK_ID), eq(userId))).thenReturn(Optional.empty());
    }

    private void givenPermissionsForUser(Work work, Long userId, boolean subAuthor, boolean subWork, List<Long> unlocked) {
        when(getWorkPermissions.execute(eq(work), eq(userId)))
                .thenReturn(WorkPermissions.createUser(subAuthor, subWork, unlocked));
    }

    private CreateEmptyChapterRequest givenCreateEmptyChapterRequest(Long workId, Long langId, String contentType) {
        CreateEmptyChapterRequest req = new CreateEmptyChapterRequest();
        req.setWorkId(workId);
        req.setLanguageId(langId);
        req.setContentType(contentType);
        return req;
    }

    private void givenCreateEmptyChapterSucceeds(Long chapterId) {
        Chapter c = new Chapter();
        c.setId(chapterId);
        when(createEmptyChapter.execute(anyLong(), anyLong(), anyString())).thenReturn(c);
    }

    private void givenCreateEmptyChapterFails(Exception e) {
        when(createEmptyChapter.execute(anyLong(), anyLong(), anyString())).thenThrow(e);
    }

    private ResponseEntity<WorkResponseDto> whenGettingWorkById(Long workId, JwtUserPrincipal principal) {
        return controller.getWorkById(workId, principal);
    }

    private ResponseEntity<CreateEmptyChapterResponse> whenCreatingEmptyChapter(CreateEmptyChapterRequest request) {
        return controller.createEmptyChapter(request);
    }

    private void thenShouldReturnOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenShouldReturnNotFound(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private void thenShouldReturnBadRequest(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private void thenWorkIdIs(WorkResponseDto dto, Long expectedId) {
        assertNotNull(dto);
        assertEquals(expectedId, dto.getId());
    }

    private void thenWorkTitleIs(WorkResponseDto dto, String expectedTitle) {
        assertNotNull(dto);
        assertEquals(expectedTitle, dto.getTitle());
    }

    private void thenPermissionsAre(WorkResponseDto dto, boolean subAuthor, boolean subWork, List<Long> unlocked) {
        assertEquals(subAuthor, dto.getSubscribedToAuthor());
        assertEquals(subWork, dto.getSubscribedToWork());
        assertEquals(unlocked, dto.getUnlockedChapters());
    }

    private void thenUseCasesWereCalledForGet(Work work, Long userId) {
        verify(obtainWorkById, times(1)).execute(eq(work.getId()), eq(userId));
        verify(getWorkPermissions, times(1)).execute(eq(work), eq(userId));
    }

    private void thenPermissionsUseCaseNotCalled() {
        verify(getWorkPermissions, never()).execute(any(), anyLong());
    }

    private void thenChapterIdIs(CreateEmptyChapterResponse body, Long expectedId) {
        assertNotNull(body);
        assertEquals(expectedId, body.getChapterId());
    }
}
