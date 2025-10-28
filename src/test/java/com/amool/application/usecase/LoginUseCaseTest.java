package com.amool.application.usecase;

import com.amool.application.port.out.AuthenticateUserPort;
import com.amool.domain.model.User;
import com.amool.application.usecases.LoginUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class LoginUseCaseTest {

    private AuthenticateUserPort authPort;
    private LoginUseCase useCase;
    
    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD = "securePassword123";
    private static final String INVALID_EMAIL = "nonexistent@example.com";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "testuser";

    @BeforeEach
    public void setUp() {
        authPort = Mockito.mock(AuthenticateUserPort.class);
        useCase = new LoginUseCase(authPort);
    }

    @Test
    public void when_ValidCredentials_ThenReturnUser() {
        User expectedUser = createUser(USER_ID, USER_NAME, VALID_EMAIL);
        when(authPort.authenticate(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Optional.of(expectedUser));

        Optional<User> result = useCase.execute(VALID_EMAIL, VALID_PASSWORD);

        assertTrue(result.isPresent());
        assertEquals(USER_ID, result.get().getId());
        assertEquals(USER_NAME, result.get().getUsername());
        assertEquals(VALID_EMAIL, result.get().getEmail());
    }

    @Test
    public void when_InvalidEmail_ThenReturnEmpty() {
        when(authPort.authenticate(INVALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Optional.empty());

        Optional<User> result = useCase.execute(INVALID_EMAIL, VALID_PASSWORD);

        assertTrue(result.isEmpty());
    }

    @Test
    public void when_InvalidPassword_ThenReturnEmpty() {
        when(authPort.authenticate(VALID_EMAIL, INVALID_PASSWORD))
            .thenReturn(Optional.empty());

        Optional<User> result = useCase.execute(VALID_EMAIL, INVALID_PASSWORD);

        assertTrue(result.isEmpty());
    }

    @Test
    public void when_NullCredentials_ThenReturnEmpty() {
        Optional<User> result = useCase.execute(null, null);
        assertTrue(result.isEmpty());
    }

    private User createUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
