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
import com.amool.application.usecases.UpdateUserUseCase;
import com.amool.domain.model.User;

public class UpdateUserUseCaseTest {

    private UpdateUserUseCase updateUserUseCase;
    private LoadUserPort loadUserPort;
    private ImagesService imagesService;

    @BeforeEach
    public void setUp() {
        loadUserPort = Mockito.mock(LoadUserPort.class);
        imagesService = Mockito.mock(ImagesService.class);
        updateUserUseCase = new UpdateUserUseCase(loadUserPort, imagesService);
    }
    
    @Test
    public void when_UpdateUser_ThenReturnTrue() {
        User testUser = Mockito.mock(User.class);
        String testPassword = "testPassword";
        
        when(loadUserPort.updateUser(any(User.class), any(String.class)))
            .thenReturn(true);
        
        boolean result = updateUserUseCase.execute(testUser, testPassword);
        
        assertTrue(result);
    }

    @Test
    public void when_UpdateUserFails_ThenReturnFalse() {
        User testUser = Mockito.mock(User.class);
        String testPassword = "testPassword";
        
        when(loadUserPort.updateUser(any(User.class), any(String.class)))
            .thenReturn(false);
        
        boolean result = updateUserUseCase.execute(testUser, testPassword);
        
        assertFalse(result);
    }
}
