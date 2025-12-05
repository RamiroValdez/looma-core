package com.amool.application.usecase;

import com.amool.application.port.out.AuthenticateUserPort;
import com.amool.domain.model.User;
import com.amool.application.usecases.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class LoginTest {

    private AuthenticateUserPort authPort;
    private Login useCase;

    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD = "securePassword123";
    private static final String INVALID_EMAIL = "nonexistent@example.com";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "testuser";

    @BeforeEach
    public void setUp() {
        authPort = Mockito.mock(AuthenticateUserPort.class);
        useCase = new Login(authPort);
    }

    private User givenValidCredentialsAuthenticate() {
        User expectedUser = createUser(USER_ID, USER_NAME, VALID_EMAIL);
        when(authPort.authenticate(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Optional.of(expectedUser));
        return expectedUser;
    }

    private void givenInvalidEmailWillNotAuthenticate() {
        when(authPort.authenticate(INVALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Optional.empty());
    }

    private void givenInvalidPasswordWillNotAuthenticate() {
        when(authPort.authenticate(VALID_EMAIL, INVALID_PASSWORD))
            .thenReturn(Optional.empty());
    }

    private void givenNullCredentialsWillNotAuthenticate() {
        when(authPort.authenticate(null, null)).thenReturn(Optional.empty());
    }

    private Optional<User> whenLogin(String email, String password) {
        return useCase.execute(email, password);
    }

    private void thenUserReturned(Optional<User> result, Long expectedId, String expectedUsername, String expectedEmail) {
        assertTrue(result.isPresent());
        assertEquals(expectedId, result.get().getId());
        assertEquals(expectedUsername, result.get().getUsername());
        assertEquals(expectedEmail, result.get().getEmail());
    }

    private void thenEmpty(Optional<User> result) {
        assertTrue(result.isEmpty());
    }

    @Test
    public void when_ValidCredentials_ThenReturnUser() {
        givenValidCredentialsAuthenticate();

        Optional<User> result = whenLogin(VALID_EMAIL, VALID_PASSWORD);

        thenUserReturned(result, USER_ID, USER_NAME, VALID_EMAIL);
    }

    @Test
    public void when_InvalidEmail_ThenReturnEmpty() {
        givenInvalidEmailWillNotAuthenticate();

        Optional<User> result = whenLogin(INVALID_EMAIL, VALID_PASSWORD);

        thenEmpty(result);
    }

    @Test
    public void when_InvalidPassword_ThenReturnEmpty() {
        givenInvalidPasswordWillNotAuthenticate();

        Optional<User> result = whenLogin(VALID_EMAIL, INVALID_PASSWORD);

        thenEmpty(result);
    }

    @Test
    public void when_NullCredentials_ThenReturnEmpty() {
        givenNullCredentialsWillNotAuthenticate();

        Optional<User> result = whenLogin(null, null);

        thenEmpty(result);
    }

    private User createUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
