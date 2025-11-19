package com.amool.application.usecases;

import com.amool.application.port.out.UserPreferencesPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SetUserPreferencesUseCaseTest {

    private UserPreferencesPort port;
    private SetUserPreferencesUseCase useCase;

    @BeforeEach
    void setUp() {
        port = mock(UserPreferencesPort.class);
        useCase = new SetUserPreferencesUseCase(port);
    }

    @Test
    void whenUserIdNull_thenThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(null, List.of(1L)));
        assertTrue(ex.getMessage().toLowerCase().contains("userid"));
    }

    @Test
    void whenCategoriesNull_thenThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(10L, null));
        assertTrue(ex.getMessage().toLowerCase().contains("categoryids"));
    }

    @Test
    void delegatesToPort() {
        useCase.execute(7L, List.of(1L,2L,3L));
        verify(port).setPreferredCategories(eq(7L), eq(List.of(1L,2L,3L)));
    }
}
