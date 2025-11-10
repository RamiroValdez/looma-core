package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.CreateEmptyChapterUseCase;
import com.amool.application.usecases.GetWorkPermissionsUseCase;
import com.amool.application.usecases.ObtainWorkByIdUseCase;
import com.amool.application.usecases.UpdateWorkPriceUseCase;
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ManageWorkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ManageWorkControllerPriceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ObtainWorkByIdUseCase obtainWorkByIdUseCase;
    @MockitoBean private CreateEmptyChapterUseCase createEmptyChapterUseCase;
    @MockitoBean private GetWorkPermissionsUseCase getWorkPermissionsUseCase;
    @MockitoBean private UpdateWorkPriceUseCase updateWorkPriceUseCase;

    private static final Long USER_ID = 100L;

    @BeforeEach
    void setUp() {
        var principal = new JwtUserPrincipal(USER_ID, "u@e.com", "Name", "Surname", "user");
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void updatePrice_returns204_onSuccess() throws Exception {
        String body = "{\"price\": 19.99}";
        mockMvc.perform(patch("/api/manage-work/{workId}/price", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNoContent());
        verify(updateWorkPriceUseCase).execute(1L, new java.math.BigDecimal("19.99"), USER_ID);
    }

    @Test
    void updatePrice_returns403_onForbidden() throws Exception {
        doThrow(new SecurityException("Forbidden")).when(updateWorkPriceUseCase)
                .execute(1L, new java.math.BigDecimal("19.99"), USER_ID);
        String body = "{\"price\": 19.99}";
        mockMvc.perform(patch("/api/manage-work/{workId}/price", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePrice_returns404_onNotFound() throws Exception {
        doThrow(new java.util.NoSuchElementException("not found")).when(updateWorkPriceUseCase)
                .execute(1L, new java.math.BigDecimal("19.99"), USER_ID);
        String body = "{\"price\": 19.99}";
        mockMvc.perform(patch("/api/manage-work/{workId}/price", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePrice_returns400_onBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("bad price")).when(updateWorkPriceUseCase)
                .execute(1L, new java.math.BigDecimal("-1"), USER_ID);
        String body = "{\"price\": -1}";
        mockMvc.perform(patch("/api/manage-work/{workId}/price", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }
}
