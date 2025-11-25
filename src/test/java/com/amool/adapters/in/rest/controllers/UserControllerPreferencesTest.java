package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.PreferencesRequest;
import com.amool.application.usecases.GetUserByIdUseCase;
import com.amool.application.usecases.UpdateUserUseCase;
import com.amool.application.usecases.SetUserPreferencesUseCase;
import com.amool.security.JwtUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerPreferencesTest {

    private GetUserByIdUseCase getUserByIdUseCase;
    private UpdateUserUseCase updateUserUseCase;
    private SetUserPreferencesUseCase setUserPreferencesUseCase;
    private UserController controller;

    @BeforeEach
    void setUp() {
        getUserByIdUseCase = Mockito.mock(GetUserByIdUseCase.class);
        updateUserUseCase = Mockito.mock(UpdateUserUseCase.class);
        setUserPreferencesUseCase = Mockito.mock(SetUserPreferencesUseCase.class);
        controller = new UserController(getUserByIdUseCase, updateUserUseCase, setUserPreferencesUseCase);
    }

    private PreferencesRequest givenPreferences(List<Long> categoryIds) {
        return new PreferencesRequest(categoryIds);
    }

    private JwtUserPrincipal givenAuthenticatedUser(Long userId) {
        return new JwtUserPrincipal(userId, "mail@test.com", "Name", "Surname", "user");
    }

    private ResponseEntity<Void> whenSetPreferences(JwtUserPrincipal principal, PreferencesRequest req) {
        return controller.setPreferences(principal, req);
    }

    private void thenStatusIs(ResponseEntity<?> resp, int expected) {
        assertEquals(expected, resp.getStatusCode().value());
    }

    private void thenPreferencesUseCaseCalled(Long userId, List<Long> ids) {
        verify(setUserPreferencesUseCase).execute(eq(userId), eq(ids));
    }

    private void thenNoPreferencesUseCaseInteractions() {
        verifyNoInteractions(setUserPreferencesUseCase);
    }

    @Test
    void setPreferences_requiresAuthPrincipal() {
        PreferencesRequest req = givenPreferences(List.of(1L,2L));

        ResponseEntity<Void> resp = whenSetPreferences(null, req);

        thenStatusIs(resp, 401);
        thenNoPreferencesUseCaseInteractions();
    }

    @Test
    void setPreferences_ok_withPrincipal() {
        PreferencesRequest req = givenPreferences(List.of(1L,2L));
        JwtUserPrincipal principal = givenAuthenticatedUser(10L);

        ResponseEntity<Void> resp = whenSetPreferences(principal, req);

        thenStatusIs(resp, 202);
        thenPreferencesUseCaseCalled(10L, List.of(1L,2L));
    }
}
