package com.amool.application.usecase;

import com.amool.application.port.out.LikePort;
import com.amool.application.usecases.UnlikeWorkUseCase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnlikeWorkUseCaseTest {

    @Mock
    private LikePort likePort;

    @InjectMocks
    private UnlikeWorkUseCase unlikeWorkUseCase;

    private final Long WORK_ID = 1L;
    private final int INITIAL_LIKES = 5;

    @Test
    void execute_ShouldDecrementLikesAndReturnNewCount() {
        when(likePort.decrementLikes(WORK_ID)).thenReturn(INITIAL_LIKES - 1);

        int result = unlikeWorkUseCase.execute(WORK_ID);

        assertEquals(INITIAL_LIKES - 1, result);
        verify(likePort, times(1)).decrementLikes(WORK_ID);
    }

    @Test
    void execute_ShouldNotGoBelowZero() {
        when(likePort.decrementLikes(WORK_ID)).thenReturn(0);

        int result = unlikeWorkUseCase.execute(WORK_ID);

        assertEquals(0, result);
        verify(likePort, times(1)).decrementLikes(WORK_ID);
    }
}
