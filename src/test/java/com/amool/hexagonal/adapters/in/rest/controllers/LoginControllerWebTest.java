package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.application.port.in.CredentialsService;
import com.amool.hexagonal.domain.model.User;
import com.amool.hexagonal.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CredentialsService credentialsService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/auth/login returns 200 with token and user when credentials are valid")
    void login_success_returnsTokenAndUser() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setName("Pepito");
        user.setSurname("Pérez");
        user.setUsername("pepito123");
        user.setEmail("pepito@example.com");

        when(credentialsService.login("pepito@example.com", "plain-pass")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("TEST_TOKEN");

        String body = "{\"email\":\"pepito@example.com\",\"password\":\"plain-pass\"}";
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("TEST_TOKEN"));
    }

    @Test
    @DisplayName("POST /api/auth/login returns 401 when credentials are invalid")
    void login_failure_returns401() throws Exception {
        when(credentialsService.login("x@x.com", "bad")).thenReturn(Optional.empty());

        String body = "{\"email\":\"x@x.com\",\"password\":\"bad\"}";
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
