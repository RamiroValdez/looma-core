package com.amool.application.usecases;

import com.amool.application.port.out.UserAccountPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class VerifyRegistrationUseCaseTest {

    private UserAccountPort userAccountPort;
    private VerifyRegistrationUseCase useCase;

    @BeforeEach
    void setUp() {
        userAccountPort = mock(UserAccountPort.class);
        useCase = new VerifyRegistrationUseCase(userAccountPort);
    }

    @Test
    void execute_returnsUserId_whenCodeValid() {
        when(userAccountPort.enableUserIfCodeValid(eq("mail@test.com"), eq("123456"))).thenReturn(42L);
        Long id = useCase.execute("mail@test.com", "123456");
        assertEquals(42L, id);
        verify(userAccountPort).enableUserIfCodeValid("mail@test.com", "123456");
    }
}
