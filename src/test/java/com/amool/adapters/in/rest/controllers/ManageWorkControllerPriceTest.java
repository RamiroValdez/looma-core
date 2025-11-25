package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.CreateEmptyChapterUseCase;
import com.amool.application.usecases.GetWorkPermissionsUseCase;
import com.amool.application.usecases.ObtainWorkByIdUseCase;
import com.amool.application.usecases.UpdateWorkUseCase;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ManageWorkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ManageWorkControllerPriceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ObtainWorkByIdUseCase obtainWorkByIdUseCase;
    @MockitoBean private CreateEmptyChapterUseCase createEmptyChapterUseCase;
    @MockitoBean private GetWorkPermissionsUseCase getWorkPermissionsUseCase;
    @MockitoBean private UpdateWorkUseCase updateWorkUseCase;

    private static final Long USER_ID = 100L;

    @BeforeEach
    void setUp() {
        var principal = new JwtUserPrincipal(USER_ID, "u@e.com", "Name", "Surname", "user");
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void givenUpdateWorkSucceeds(Long workId) {
        when(updateWorkUseCase.execute(eq(workId), any(), anySet(), anySet(), anyString())).thenReturn(true);
    }

    private void givenUpdateWorkThrows(Long workId) {
        when(updateWorkUseCase.execute(eq(workId), any(), anySet(), anySet(), anyString()))
                .thenThrow(new RuntimeException("boom"));
    }

    private void givenUpdateWorkSucceedsWithScaledPriceAndEmptySets(Long workId, BigDecimal expectedPrice, String expectedState) {
        when(updateWorkUseCase.execute(eq(workId),
                argThat(bd -> bd != null && bd.compareTo(expectedPrice) == 0),
                eq(Collections.emptySet()),
                eq(Collections.emptySet()),
                eq(expectedState)))
                .thenReturn(true);
    }

    private ResultActions whenUpdateWorkIsPut(Long workId, String jsonBody) throws Exception {
        return mockMvc.perform(
                put("/api/manage-work/{workId}", workId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        );
    }

    private void thenOkWithTrue(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
              .andExpect(content().string("true"));
    }

    private void thenBadRequest(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest());
    }

    private void thenUseCaseCalledWithState(Long workId, String state) {
        verify(updateWorkUseCase, times(1))
                .execute(eq(workId), any(), anySet(), anySet(), eq(state));
    }

    private void thenUseCaseCalledWithScaledPriceAndEmptySets(Long workId, BigDecimal expectedPrice, String state) {
        verify(updateWorkUseCase, times(1)).execute(eq(workId),
                argThat(bd -> bd != null && bd.compareTo(expectedPrice) == 0),
                eq(Collections.emptySet()),
                eq(Collections.emptySet()),
                eq(state));
    }

    private String requestBody(BigDecimal price, String state, String[] tagIds, String[] categoryIds) {
        String tags = Arrays.stream(tagIds)
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(","));
        String categories = Arrays.stream(categoryIds)
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(","));
        return "{" +
                "\"price\": " + price + ", " +
                "\"state\": \"" + state + "\", " +
                "\"tagIds\": [" + tags + "], " +
                "\"categoryIds\": [" + categories + "]" +
                "}";
    }

    @Test
    void updateWork_put_returns200True_whenUseCaseOk() throws Exception {
        Long workId = 1L;
        givenUpdateWorkSucceeds(workId);
        String body = requestBody(new BigDecimal("9.99"), "PUBLISHED", new String[]{"t1", "t2"}, new String[]{});

        ResultActions result = whenUpdateWorkIsPut(workId, body);

        thenOkWithTrue(result);
        thenUseCaseCalledWithState(workId, "PUBLISHED");
    }

    @Test
    void updateWork_put_returns400_whenUseCaseThrows() throws Exception {
        Long workId = 1L;
        givenUpdateWorkThrows(workId);
        String body = requestBody(new BigDecimal("5.00"), "DRAFT", new String[]{}, new String[]{});

        ResultActions result = whenUpdateWorkIsPut(workId, body);

        thenBadRequest(result);
    }

    @Test
    void updateWork_put_returns200True_withScaledPriceAndEmptySets() throws Exception {
        Long workId = 2L;
        BigDecimal expected = new BigDecimal("9.99");
        givenUpdateWorkSucceedsWithScaledPriceAndEmptySets(workId, expected, "PUBLISHED");
        String body = requestBody(new BigDecimal("9.9900"), "PUBLISHED", new String[]{}, new String[]{});

        ResultActions result = whenUpdateWorkIsPut(workId, body);

        thenOkWithTrue(result);
        thenUseCaseCalledWithScaledPriceAndEmptySets(workId, expected, "PUBLISHED");
    }
}
