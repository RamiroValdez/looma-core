package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.RegisterRequest;
import com.amool.application.usecases.GetUserByIdUseCase;
import com.amool.application.usecases.StartRegistrationUseCase;
import com.amool.application.usecases.VerifyRegistrationUseCase;
import com.amool.domain.model.User;
import com.amool.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegistrationControllerTest {

    private StartRegistrationUseCase startRegistrationUseCase;
    private VerifyRegistrationUseCase verifyRegistrationUseCase;
    private GetUserByIdUseCase getUserByIdUseCase;
    private JwtService jwtService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        startRegistrationUseCase = mock(StartRegistrationUseCase.class);
        verifyRegistrationUseCase = mock(VerifyRegistrationUseCase.class);
        getUserByIdUseCase = mock(GetUserByIdUseCase.class);
        jwtService = mock(JwtService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new RegistrationController(startRegistrationUseCase, verifyRegistrationUseCase, getUserByIdUseCase, jwtService)
        ).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_returns202_and_callsUseCase() throws Exception {
        RegisterRequest req = new RegisterRequest("Nombre","Apellido","user","mail@test.com","Pass1!","Pass1!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted());

        ArgumentCaptor<String> nameCap = ArgumentCaptor.forClass(String.class);
        verify(startRegistrationUseCase).execute(
                nameCap.capture(), anyString(), anyString(), anyString(), anyString(), anyString());
        assertEquals("Nombre", nameCap.getValue());
    }

    @Test
    void verify_returns201_withToken_whenUserFound() throws Exception {
        when(verifyRegistrationUseCase.execute("mail@test.com","123456")).thenReturn(1L);
        User u = new User();
        u.setId(1L); u.setEmail("mail@test.com"); u.setName("Nombre"); u.setSurname("Apellido"); u.setUsername("user");
        when(getUserByIdUseCase.execute(1L)).thenReturn(Optional.of(u));
        when(jwtService.generateToken(any(HashMap.class))).thenReturn("jwt-token");

        String json = "{\"email\":\"mail@test.com\",\"code\":\"123456\"}";
        mockMvc.perform(post("/api/auth/register/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", equalTo("jwt-token")));
    }

    @Test
    void verify_returns201_emptyBody_whenUserNotFound() throws Exception {
        when(verifyRegistrationUseCase.execute("mail@test.com","123456")).thenReturn(99L);
        when(getUserByIdUseCase.execute(99L)).thenReturn(Optional.empty());

        String json = "{\"email\":\"mail@test.com\",\"code\":\"123456\"}";
        mockMvc.perform(post("/api/auth/register/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }
}
