package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.AuthResponse;
import com.amool.adapters.in.rest.dtos.LoginRequest;
import com.amool.adapters.in.rest.dtos.UserDto;
import com.amool.application.usecases.LoginUseCase;
import com.amool.domain.model.User;
import com.amool.security.JwtService;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    private LoginController controller;
    private LoginUseCase loginUseCase;
    private JwtService jwtService;

    private static final Long USER_ID = 42L;
    private static final String EMAIL = "user@example.com";
    private static final String NAME = "John";
    private static final String SURNAME = "Doe";
    private static final String USERNAME = "johnd";
    private static final String PASSWORD = "s3cret";
    private static final String TOKEN = "jwt.token.value";

    @BeforeEach
    void setUp() {
        loginUseCase = Mockito.mock(LoginUseCase.class);
        jwtService = Mockito.mock(JwtService.class);
        controller = new LoginController(loginUseCase, jwtService);
    }

    // ========== Tests for /me ==========

    @Test
    @DisplayName("GET /api/auth/me - Should return user info when authenticated")
    void me_shouldReturnUser_whenAuthenticated() {
        // Given
        JwtUserPrincipal principal = givenAuthenticatedPrincipal();

        // When
        ResponseEntity<UserDto> response = whenCallingMe(principal);

        // Then
        thenShouldReturnOk(response);
        thenBodyMatchesUserDto(response, USER_ID, EMAIL, NAME, SURNAME, USERNAME);
        thenNoTokenWasGenerated();
    }

    @Test
    @DisplayName("GET /api/auth/me - Should return 401 when not authenticated")
    void me_shouldReturn401_whenNoPrincipal() {
        // When
        ResponseEntity<UserDto> response = whenCallingMe(null);

        // Then
        thenShouldReturnUnauthorized(response);
        thenNoTokenWasGenerated();
    }

    // ========== Tests for /login ==========

    @Test
    @DisplayName("POST /api/auth/login - Should return token when credentials are valid")
    void login_shouldReturnToken_whenCredentialsValid() {
        // Given
        LoginRequest request = givenLoginRequest(EMAIL, PASSWORD);
        User user = givenUser(USER_ID, EMAIL, NAME, SURNAME, USERNAME);
        givenLoginSucceedsWith(user);
        givenJwtWillReturnToken(TOKEN);

        // When
        ResponseEntity<AuthResponse> response = whenLoggingIn(request);

        // Then
        thenShouldReturnOk(response);
        thenBodyHasToken(response, TOKEN);
        thenLoginWasCalledWith(EMAIL, PASSWORD);
        thenTokenWasGeneratedWithClaimsFor(user);
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 401 when credentials are invalid")
    void login_shouldReturn401_whenCredentialsInvalid() {
        // Given
        LoginRequest request = givenLoginRequest(EMAIL, PASSWORD);
        givenLoginFails();

        // When
        ResponseEntity<AuthResponse> response = whenLoggingIn(request);

        // Then
        thenShouldReturnUnauthorized(response);
        thenNoTokenWasGenerated();
    }

    // ===== Given =====
    private JwtUserPrincipal givenAuthenticatedPrincipal() {
        return new JwtUserPrincipal(USER_ID, EMAIL, NAME, SURNAME, USERNAME);
    }

    private LoginRequest givenLoginRequest(String email, String password) {
        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }

    private User givenUser(Long id, String email, String name, String surname, String username) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setName(name);
        u.setSurname(surname);
        u.setUsername(username);
        return u;
    }

    private void givenLoginSucceedsWith(User user) {
        when(loginUseCase.execute(anyString(), anyString())).thenReturn(Optional.of(user));
    }

    private void givenLoginFails() {
        when(loginUseCase.execute(anyString(), anyString())).thenReturn(Optional.empty());
    }

    private void givenJwtWillReturnToken(String token) {
        when(jwtService.generateToken(anyMap())).thenReturn(token);
    }

    // ===== When =====
    private ResponseEntity<UserDto> whenCallingMe(JwtUserPrincipal principal) {
        return controller.me(principal);
    }

    private ResponseEntity<AuthResponse> whenLoggingIn(LoginRequest request) {
        return controller.login(request);
    }

    // ===== Then =====
    private void thenShouldReturnOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void thenShouldReturnUnauthorized(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private void thenBodyMatchesUserDto(ResponseEntity<UserDto> response, Long id, String email, String name, String surname, String username) {
        assertNotNull(response.getBody());
        UserDto dto = response.getBody();
        assertEquals(id, dto.getId());
        assertEquals(email, dto.getEmail());
        assertEquals(name, dto.getName());
        assertEquals(surname, dto.getSurname());
        assertEquals(username, dto.getUsername());
    }

    private void thenBodyHasToken(ResponseEntity<AuthResponse> response, String expectedToken) {
        assertNotNull(response.getBody());
        assertEquals(expectedToken, response.getBody().getToken());
    }

    private void thenLoginWasCalledWith(String email, String password) {
        verify(loginUseCase, times(1)).execute(eq(email), eq(password));
    }

    private void thenTokenWasGeneratedWithClaimsFor(User user) {
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(jwtService, times(1)).generateToken(captor.capture());
        Map<String, Object> claims = captor.getValue();
        assertEquals(user.getId(), claims.get("userId"));
        assertEquals(user.getEmail(), claims.get("email"));
        assertEquals(user.getName(), claims.get("name"));
        assertEquals(user.getSurname(), claims.get("surname"));
        assertEquals(user.getUsername(), claims.get("username"));
    }

    private void thenNoTokenWasGenerated() {
        verify(jwtService, never()).generateToken(anyMap());
    }
}
