package com.amool.application.usecase;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;
import com.amool.application.usecases.GetUserById;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GetUserByIdTest {

    private LoadUserPort loadUserPort;
    private FilesStoragePort filesStoragePort;
    private GetUserById useCase;
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "testuser";
    private static final String USER_EMAIL = "test@example.com";

    @BeforeEach
    public void setUp() {
        loadUserPort = Mockito.mock(LoadUserPort.class);
        filesStoragePort = Mockito.mock(FilesStoragePort.class);
        useCase = new GetUserById(loadUserPort, filesStoragePort);
    }

    private User givenUserExists(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        when(loadUserPort.getById(id)).thenReturn(Optional.of(user));
        return user;
    }

    private void givenUserDoesNotExist(Long id) {
        when(loadUserPort.getById(id)).thenReturn(Optional.empty());
    }

    private Optional<User> whenGetUserById(Long id) {
        return useCase.execute(id);
    }

    private void thenUserFound(Optional<User> result, Long expectedId, String expectedUsername, String expectedEmail) {
        assertTrue(result.isPresent(), "Se esperaba usuario presente");
        assertEquals(expectedId, result.get().getId());
        assertEquals(expectedUsername, result.get().getUsername());
        assertEquals(expectedEmail, result.get().getEmail());
    }

    private void thenUserEmpty(Optional<User> result) {
        assertTrue(result.isEmpty(), "Se esperaba Optional vacío");
    }

    @Test
    public void when_UserExists_ThenReturnUser() {
        givenUserExists(USER_ID, USER_NAME, USER_EMAIL);

        Optional<User> result = whenGetUserById(USER_ID);

        thenUserFound(result, USER_ID, USER_NAME, USER_EMAIL);
    }

    @Test
    public void when_UserDoesNotExist_ThenReturnEmpty() {
        givenUserDoesNotExist(USER_ID);

        Optional<User> result = whenGetUserById(USER_ID);

        thenUserEmpty(result);
    }
}
