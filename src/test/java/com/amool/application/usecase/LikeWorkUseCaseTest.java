package com.amool.application.usecase;

import com.amool.application.port.out.LikePort;
import com.amool.application.usecases.LikeWorkUseCase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeWorkUseCaseTest {

    @Mock
    private LikePort likePort;

    @InjectMocks
    private LikeWorkUseCase likeWorkUseCase;

    private final Long WORK_ID = 1L;
    private final int INITIAL_LIKES = 5;

    @Test
    void execute_ShouldIncrementLikesAndReturnNewCount() {
        when(likePort.incrementLikes(WORK_ID)).thenReturn(INITIAL_LIKES + 1);

        int result = likeWorkUseCase.execute(WORK_ID);

        assertEquals(INITIAL_LIKES + 1, result);
        verify(likePort, times(1)).incrementLikes(WORK_ID);
    }

    @Test
    void execute_ShouldReturnZeroWhenNoPreviousLikes() {
        when(likePort.incrementLikes(WORK_ID)).thenReturn(1);

        int result = likeWorkUseCase.execute(WORK_ID);
        
        assertEquals(1, result);
        verify(likePort, times(1)).incrementLikes(WORK_ID);
    }
}
