package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amool.application.service.ImagesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.amool.application.port.out.LoadUserPort;
import com.amool.application.usecases.UpdateUser;
import com.amool.domain.model.User;

public class UpdateUserTest {

    private static final String TEST_PASSWORD = "testPassword";

    private UpdateUser updateUser;
    private LoadUserPort loadUserPort;
    private ImagesService imagesService;

    @BeforeEach
    public void setUp() {
        loadUserPort = Mockito.mock(LoadUserPort.class);
        imagesService = Mockito.mock(ImagesService.class);
        updateUser = new UpdateUser(loadUserPort, imagesService);
    }
    
    @Test
    public void shouldReturnTrueWhenUpdateSucceeds() {
        User user = givenUser();
        givenUpdateResult(user, TEST_PASSWORD, true);

        boolean result = whenUpdatingUser(user, TEST_PASSWORD);

        thenUpdateSucceeds(result);
    }

    @Test
    public void shouldReturnFalseWhenUpdateFails() {
        User user = givenUser();
        givenUpdateResult(user, TEST_PASSWORD, false);

        boolean result = whenUpdatingUser(user, TEST_PASSWORD);

        thenUpdateFails(result);
    }

    private User givenUser() {
        return Mockito.mock(User.class);
    }

    private void givenUpdateResult(User user, String password, boolean expectedResult) {
        when(loadUserPort.updateUser(user, password)).thenReturn(expectedResult);
    }

    private boolean whenUpdatingUser(User user, String password) {
        return updateUser.execute(user, password);
    }

    private void thenUpdateSucceeds(boolean result) {
        assertTrue(result);
    }

    private void thenUpdateFails(boolean result) {
        assertFalse(result);
    }
}
