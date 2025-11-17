package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.CreateEmptyChapterUseCase;
import com.amool.application.usecases.GetWorkPermissionsUseCase;
import com.amool.application.usecases.ObtainWorkByIdUseCase;
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

    private static final Long USER_ID = 100L;

    @BeforeEach
    void setUp() {
        var principal = new JwtUserPrincipal(USER_ID, "u@e.com", "Name", "Surname", "user");
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
