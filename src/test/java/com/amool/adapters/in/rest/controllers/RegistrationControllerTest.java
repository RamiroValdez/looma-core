package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.RegisterRequest;
import com.amool.application.usecases.GetUserById;
import com.amool.application.usecases.StartRegistration;
import com.amool.application.usecases.VerifyRegistration;
import com.amool.domain.model.User;
import com.amool.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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

    private StartRegistration startRegistration;
    private VerifyRegistration verifyRegistration;
    private GetUserById getUserById;
    private JwtService jwtService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        startRegistration = mock(StartRegistration.class);
        verifyRegistration = mock(VerifyRegistration.class);
        getUserById = mock(GetUserById.class);
        jwtService = mock(JwtService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new RegistrationController(startRegistration, verifyRegistration, getUserById, jwtService)
        ).build();
        objectMapper = new ObjectMapper();
    }

    private RegisterRequest givenRegisterRequest() {
        return new RegisterRequest("Nombre","Apellido","user","mail@test.com","Pass1!","Pass1!");
    }

    private void givenVerifyReturnsUserId(String email, String code, Long userId) {
        when(verifyRegistration.execute(email, code)).thenReturn(userId);
    }

    private void givenUserFound(Long userId, String email, String name, String surname, String username) {
        User u = new User();
        u.setId(userId);
        u.setEmail(email);
        u.setName(name);
        u.setSurname(surname);
        u.setUsername(username);
        when(getUserById.execute(userId)).thenReturn(Optional.of(u));
    }

    private void givenUserNotFound(Long userId) {
        when(getUserById.execute(userId)).thenReturn(Optional.empty());
    }

    private void givenJwtWillBeGenerated(String token) {
        when(jwtService.generateToken(any(HashMap.class))).thenReturn(token);
    }

    private String givenVerifyJson(String email, String code) {
        return "{\"email\":\"" + email + "\",\"code\":\"" + code + "\"}";
    }

    private ResultActions whenRegister(RegisterRequest req) throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
    }

    private ResultActions whenVerify(String jsonBody) throws Exception {
        return mockMvc.perform(post("/api/auth/register/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));
    }

    private void thenStatusAccepted(ResultActions actions) throws Exception {
        actions.andExpect(status().isAccepted());
    }

    private void thenStatusCreated(ResultActions actions) throws Exception {
        actions.andExpect(status().isCreated());
    }

    private void thenResponseHasToken(ResultActions actions, String token) throws Exception {
        actions.andExpect(jsonPath("$.token", equalTo(token)));
    }

    private void thenStartRegistrationCalledWithName(String expectedName) {
        ArgumentCaptor<String> nameCap = ArgumentCaptor.forClass(String.class);
        verify(startRegistration).execute(nameCap.capture(), anyString(), anyString(), anyString(), anyString(), anyString());
        assertEquals(expectedName, nameCap.getValue());
    }

    private void thenVerifyCalled(String email, String code) {
        verify(verifyRegistration).execute(eq(email), eq(code));
    }

    private void thenJwtGenerated() {
        verify(jwtService).generateToken(any(HashMap.class));
    }

    private void thenGetUserByIdCalled(Long userId) {
        verify(getUserById).execute(userId);
    }

    private void thenNoTokenInResponse(ResultActions actions) {
    }

    @Test
    void register_returns202_and_callsUseCase() throws Exception {
        RegisterRequest req = givenRegisterRequest();

        ResultActions actions = whenRegister(req);

        thenStatusAccepted(actions);
        thenStartRegistrationCalledWithName("Nombre");
    }

    @Test
    void verify_returns201_withToken_whenUserFound() throws Exception {
        String email = "mail@test.com"; String code = "123456"; Long userId = 1L;
        givenVerifyReturnsUserId(email, code, userId);
        givenUserFound(userId, email, "Nombre", "Apellido", "user");
        givenJwtWillBeGenerated("jwt-token");
        String json = givenVerifyJson(email, code);

        ResultActions actions = whenVerify(json);

        thenStatusCreated(actions);
        thenResponseHasToken(actions, "jwt-token");
        thenVerifyCalled(email, code);
        thenGetUserByIdCalled(userId);
        thenJwtGenerated();
    }

    @Test
    void verify_returns201_emptyBody_whenUserNotFound() throws Exception {
        String email = "mail@test.com"; String code = "123456"; Long userId = 99L;
        givenVerifyReturnsUserId(email, code, userId);
        givenUserNotFound(userId);
        String json = givenVerifyJson(email, code);

        ResultActions actions = whenVerify(json);

        thenStatusCreated(actions);
        thenVerifyCalled(email, code);
        thenGetUserByIdCalled(userId);
        thenNoTokenInResponse(actions);
    }
}
