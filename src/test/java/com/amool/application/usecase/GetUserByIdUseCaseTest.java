package com.amool.application.usecase;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;
import com.amool.application.usecases.GetUserByIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GetUserByIdUseCaseTest {

    private LoadUserPort loadUserPort;
    private AwsS3Port awsS3Port;
    private GetUserByIdUseCase useCase;
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "testuser";
    private static final String USER_EMAIL = "test@example.com";

    @BeforeEach
    public void setUp() {
        loadUserPort = Mockito.mock(LoadUserPort.class);
        awsS3Port = Mockito.mock(AwsS3Port.class);
        useCase = new GetUserByIdUseCase(loadUserPort, awsS3Port);
    }

    @Test
    public void when_UserExists_ThenReturnUser() {
        User expectedUser = new User();
        expectedUser.setId(USER_ID);
        expectedUser.setUsername(USER_NAME);
        expectedUser.setEmail(USER_EMAIL);
        
        when(loadUserPort.getById(USER_ID)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = useCase.execute(USER_ID);

        assertTrue(result.isPresent());
        assertEquals(USER_ID, result.get().getId());
        assertEquals(USER_NAME, result.get().getUsername());
        assertEquals(USER_EMAIL, result.get().getEmail());
    }

    @Test
    public void when_UserDoesNotExist_ThenReturnEmpty() {
        when(loadUserPort.getById(USER_ID)).thenReturn(Optional.empty());

        Optional<User> result = useCase.execute(USER_ID);

        assertTrue(result.isEmpty());
    }

}
