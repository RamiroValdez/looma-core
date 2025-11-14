package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.GetAllWorksUseCase;
import com.amool.application.usecases.ToggleWorkLikeUseCase;
import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.domain.model.Work;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WorkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WorkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private GetAllWorksUseCase getAllWorksUseCase;
    @MockitoBean private ToggleWorkLikeUseCase toggleWorkLikeUseCase;

    private static final Long USER_ID = 77L;

    @BeforeEach
    void setUp() {
        var principal = new JwtUserPrincipal(USER_ID, "user@example.com", "Name", "Surname", "user");
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ========== GET /api/works ==========

    @Test
    @DisplayName("GET /api/works - 200 OK returns list of works")
    void getAllWorks_returns200_withList() throws Exception {
        givenWorksExist(List.of(givenWork(1L, "Title A"), givenWork(2L, "Title B")));

        ResultActions response = whenClientRequestsAllWorks();

        thenResponseIsOkWithWorks(response, 2);
        response.andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Title A"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Title B"));
        thenGetAllWorksUseCaseWasCalled();
    }

    @Test
    @DisplayName("GET /api/works - 200 OK returns empty list when no works")
    void getAllWorks_returns200_emptyList() throws Exception {
        givenWorksExist(List.of());

        ResultActions response = whenClientRequestsAllWorks();

        thenResponseIsOkWithWorks(response, 0);
        thenGetAllWorksUseCaseWasCalled();
    }

    // ========== POST /api/works/{id}/like ==========

    @Test
    @DisplayName("POST /api/works/{id}/like - 200 OK toggles like")
    void toggleLike_returns200_onSuccess() throws Exception {
        givenToggleLikeSucceeds(5L, new LikeResponseDto(5L, 10L, true));

        ResultActions response = whenClientTogglesLike(5L);

        thenResponseIsOkWithLike(response, 5L, 10L, true);
        thenToggleLikeUseCaseWasCalled(5L);
    }

    @Test
    @DisplayName("POST /api/works/{id}/like - 404 Not Found when work missing")
    void toggleLike_returns404_onNotFound() throws Exception {
        givenToggleLikeThrowsNotFound(99L);

        ResultActions response = whenClientTogglesLike(99L);

        thenStatusIsNotFound(response);
    }

    @Test
    @DisplayName("POST /api/works/{id}/like - 403 Forbidden when not allowed")
    void toggleLike_returns403_onForbidden() throws Exception {
        givenToggleLikeThrowsForbidden(6L);

        ResultActions response = whenClientTogglesLike(6L);

        thenStatusIsForbidden(response);
    }

    @Test
    @DisplayName("POST /api/works/{id}/like - 400 Bad Request on illegal argument")
    void toggleLike_returns400_onIllegalArgument() throws Exception {
        givenToggleLikeThrowsBadRequest(7L);

        ResultActions response = whenClientTogglesLike(7L);

        thenStatusIsBadRequest(response);
    }

    // ===== Given =====
    private void givenWorksExist(List<Work> works) {
        when(getAllWorksUseCase.execute()).thenReturn(works);
    }

    private Work givenWork(Long id, String title) {
        Work w = new Work();
        w.setId(id);
        w.setTitle(title);
        w.setDescription("Desc " + title);
        w.setPrice(new BigDecimal("9.99"));
        w.setLikes(5);
        w.setAverageRating(4.0);
        w.setLikedByUser(false);
        return w;
    }

    private void givenToggleLikeSucceeds(Long workId, LikeResponseDto dto) {
        when(toggleWorkLikeUseCase.execute(eq(workId), eq(USER_ID))).thenReturn(dto);
    }

    private void givenToggleLikeThrowsNotFound(Long workId) {
        when(toggleWorkLikeUseCase.execute(eq(workId), eq(USER_ID)))
                .thenThrow(new java.util.NoSuchElementException("not found"));
    }

    private void givenToggleLikeThrowsForbidden(Long workId) {
        when(toggleWorkLikeUseCase.execute(eq(workId), eq(USER_ID)))
                .thenThrow(new SecurityException("forbidden"));
    }

    private void givenToggleLikeThrowsBadRequest(Long workId) {
        when(toggleWorkLikeUseCase.execute(eq(workId), eq(USER_ID)))
                .thenThrow(new IllegalArgumentException("bad"));
    }

    // ===== When =====
    private ResultActions whenClientRequestsAllWorks() throws Exception {
        return mockMvc.perform(get("/api/works")
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions whenClientTogglesLike(Long workId) throws Exception {
        return mockMvc.perform(post("/api/works/{workId}/like", workId)
                .accept(MediaType.APPLICATION_JSON));
    }

    // ===== Then =====
    private void thenResponseIsOkWithWorks(ResultActions response, int expectedSize) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));
    }

    private void thenResponseIsOkWithLike(ResultActions response, Long workId, Long likeCount, boolean likedByUser) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.workId").value(workId))
                .andExpect(jsonPath("$.likeCount").value(likeCount))
                .andExpect(jsonPath("$.likedByUser").value(likedByUser));
    }

    private void thenStatusIsNotFound(ResultActions response) throws Exception {
        response.andExpect(status().isNotFound());
    }

    private void thenStatusIsForbidden(ResultActions response) throws Exception {
        response.andExpect(status().isForbidden());
    }

    private void thenStatusIsBadRequest(ResultActions response) throws Exception {
        response.andExpect(status().isBadRequest());
    }

    private void thenGetAllWorksUseCaseWasCalled() {
        verify(getAllWorksUseCase).execute();
    }

    private void thenToggleLikeUseCaseWasCalled(Long workId) {
        verify(toggleWorkLikeUseCase).execute(eq(workId), eq(USER_ID));
    }
}
