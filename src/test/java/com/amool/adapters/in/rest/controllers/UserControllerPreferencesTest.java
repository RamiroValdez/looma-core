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

    @Test
    void setPreferences_requiresAuthPrincipal() {
        PreferencesRequest req = new PreferencesRequest(List.of(1L,2L));
        ResponseEntity<Void> resp = controller.setPreferences(null, req);
        assertEquals(401, resp.getStatusCode().value());
    }

    @Test
    void setPreferences_ok_withPrincipal() {
        PreferencesRequest req = new PreferencesRequest(List.of(1L,2L));
        JwtUserPrincipal principal = new JwtUserPrincipal(10L, "mail@test.com", "Name", "Surname", "user");

        ResponseEntity<Void> resp = controller.setPreferences(principal, req);
        assertEquals(202, resp.getStatusCode().value());
        verify(setUserPreferencesUseCase).execute(eq(10L), eq(List.of(1L,2L)));
    }
}
