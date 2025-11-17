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

    @Test
    void updateWork_put_returns200True_whenUseCaseOk() throws Exception {
        when(updateWorkUseCase.execute(eq(1L), any(), anySet(), anyString())).thenReturn(true);

        mockMvc.perform(
                put("/api/manage-work/{workId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\": 9.99, \"state\": \"PUBLISHED\", \"tagIds\": [\"t1\", \"t2\"]}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(updateWorkUseCase, times(1)).execute(eq(1L), any(), anySet(), eq("PUBLISHED"));
    }

    @Test
    void updateWork_put_returns400_whenUseCaseThrows() throws Exception {
        when(updateWorkUseCase.execute(eq(1L), any(), anySet(), anyString())).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(
                put("/api/manage-work/{workId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\": 5.00, \"state\": \"DRAFT\", \"tagIds\": []}"))
                .andExpect(status().isBadRequest());
    }
}
