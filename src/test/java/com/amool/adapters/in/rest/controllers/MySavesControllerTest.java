package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.SaveWorkResponseDto;
import com.amool.application.usecases.GetSavedWorks;
import com.amool.application.usecases.IsWorkSaved;
import com.amool.application.usecases.ToggleSaveWork;
import com.amool.domain.model.Work;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MySavesControllerTest {

    private MySavesController controller;
    private ToggleSaveWork toggleSaveWork;
    private IsWorkSaved isWorkSaved;
    private GetSavedWorks getSavedWorks;

    private static final Long USER_ID = 42L;
    private static final Long WORK_ID = 5L;

    @BeforeEach
    void setUp() {
        toggleSaveWork = Mockito.mock(ToggleSaveWork.class);
        isWorkSaved = Mockito.mock(IsWorkSaved.class);
        getSavedWorks = Mockito.mock(GetSavedWorks.class);
        controller = new MySavesController(toggleSaveWork, isWorkSaved, getSavedWorks);
    }

    @Test
    @DisplayName("POST /api/saved-works/{workId}/toggle - Debe alternar y devolver estado actual")
    void toggleSaveWork_shouldToggleAndReturnStatus() {
        JwtUserPrincipal principal = givenAuthenticatedPrincipal(USER_ID);
        givenIsWorkSavedWillReturn(true);

        ResponseEntity<?> response = whenTogglingSave(principal, WORK_ID);

        thenShouldReturnOk(response);
        thenResponseHasSaveStatus(response, WORK_ID, true);
        thenToggleAndCheckWereCalled(USER_ID, WORK_ID);
    }

    @Test
    @DisplayName("GET /api/saved-works/{workId}/status - Debe devolver estado actual")
    void getSaveStatus_shouldReturnCurrentStatus() {
        JwtUserPrincipal principal = givenAuthenticatedPrincipal(USER_ID);
        givenIsWorkSavedWillReturn(false);

        ResponseEntity<?> response = whenGettingStatus(principal, WORK_ID);

        thenShouldReturnOk(response);
        thenResponseHasSaveStatus(response, WORK_ID, false);
        thenIsSavedWasCalled(USER_ID, WORK_ID);
    }

    @Test
    @DisplayName("GET /api/saved-works - Debe devolver lista de obras guardadas del usuario")
    void getSavedWorks_shouldReturnUserSavedWorks() {
        JwtUserPrincipal principal = givenAuthenticatedPrincipal(USER_ID);
        List<Work> saved = givenSavedWorks(
                givenWork(1L, "Work A"),
                givenWork(2L, "Work B")
        );
        givenGetSavedWorksWillReturn(saved);

        ResponseEntity<List<Work>> response = whenGettingSavedWorks(principal);

        thenShouldReturnOk(response);
        thenBodyIsSameInstance(response, saved);
        thenGetSavedWorksWasCalled(USER_ID);
    }

    private JwtUserPrincipal givenAuthenticatedPrincipal(Long userId) {
        return new JwtUserPrincipal(userId, "user@example.com", "Name", "Surname", "username");
    }

    private void givenIsWorkSavedWillReturn(boolean isSaved) {
        when(isWorkSaved.execute(eq(USER_ID), eq(WORK_ID))).thenReturn(isSaved);
    }

    private Work givenWork(Long id, String title) {
        Work w = new Work();
        w.setId(id);
        w.setTitle(title);
        return w;
    }

    private List<Work> givenSavedWorks(Work... works) {
        return List.of(works);
    }

    private void givenGetSavedWorksWillReturn(List<Work> works) {
        when(getSavedWorks.execute(eq(USER_ID))).thenReturn(works);
    }

    private ResponseEntity<?> whenTogglingSave(JwtUserPrincipal principal, Long workId) {
        return controller.toggleSaveWork(principal, workId);
    }

    private ResponseEntity<?> whenGettingStatus(JwtUserPrincipal principal, Long workId) {
        return controller.getSaveStatus(principal, workId);
    }

    private ResponseEntity<List<Work>> whenGettingSavedWorks(JwtUserPrincipal principal) {
        return controller.getSavedWorks(principal);
    }

    private void thenShouldReturnOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenResponseHasSaveStatus(ResponseEntity<?> response, Long expectedWorkId, boolean expectedIsSaved) {
        assertTrue(response.getBody() instanceof SaveWorkResponseDto);
        SaveWorkResponseDto dto = (SaveWorkResponseDto) response.getBody();
        assertEquals(expectedWorkId, dto.workId());
        assertEquals(expectedIsSaved, dto.isSaved());
    }

    private void thenToggleAndCheckWereCalled(Long userId, Long workId) {
        verify(toggleSaveWork, times(1)).execute(eq(userId), eq(workId));
        verify(isWorkSaved, times(1)).execute(eq(userId), eq(workId));
    }

    private void thenIsSavedWasCalled(Long userId, Long workId) {
        verify(isWorkSaved, times(1)).execute(eq(userId), eq(workId));
    }

    private void thenBodyIsSameInstance(ResponseEntity<List<Work>> response, List<Work> expected) {
        assertSame(expected, response.getBody());
        assertEquals(expected.size(), response.getBody().size());
    }

    private void thenGetSavedWorksWasCalled(Long userId) {
        verify(getSavedWorks, times(1)).execute(eq(userId));
    }
}
