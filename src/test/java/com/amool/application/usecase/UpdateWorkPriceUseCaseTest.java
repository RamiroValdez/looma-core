package com.amool.application.usecase;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.WorkPort;
import com.amool.application.usecases.UpdateWorkPriceUseCase;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateWorkPriceUseCaseTest {

    @Mock
    private ObtainWorkByIdPort obtainWorkByIdPort;

    @Mock
    private WorkPort workPort;

    private UpdateWorkPriceUseCase useCase;

    private final Long workId = 1L;
    private final Long ownerId = 10L;

    private Work work;

    @BeforeEach
    public void setUp() {
        useCase = new UpdateWorkPriceUseCase(obtainWorkByIdPort, workPort);
        work = new Work();
        work.setId(workId);
        User owner = new User();
        owner.setId(ownerId);
        work.setCreator(owner);
    }

    @Test
    public void when_ValidOwnerAndPrice_ThenUpdate() {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));

        useCase.execute(workId, new BigDecimal("9.99"), ownerId);

        assertEquals(new BigDecimal("9.99"), work.getPrice());
        verify(workPort).updateWork(work);
    }

    @Test
    public void when_NotAuthenticated_ThenSecurityException() {
        assertThrows(SecurityException.class, () -> useCase.execute(workId, BigDecimal.ONE, null));
        verifyNoInteractions(obtainWorkByIdPort, workPort);
    }

    @Test
    public void when_WorkNotFound_ThenNoSuchElement() {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> useCase.execute(workId, BigDecimal.ONE, ownerId));
    }

    @Test
    public void when_NotOwner_ThenSecurityException() {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));
        assertThrows(SecurityException.class, () -> useCase.execute(workId, BigDecimal.ONE, 999L));
        verify(workPort, never()).updateWork(any());
    }

    @Test
    public void when_InvalidPrice_ThenIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(workId, new BigDecimal("-1"), ownerId));
        verifyNoInteractions(obtainWorkByIdPort, workPort);
    }
}
