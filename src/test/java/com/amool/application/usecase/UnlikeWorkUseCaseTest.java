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

    private static final Long WORK_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long INITIAL_LIKES = 5L;

    @Test
    void execute_ShouldDecrementLikesAndReturnNewCount() {
        Long expectedLikes = 4L;
        when(likePort.unlikeWork(WORK_ID, USER_ID)).thenReturn(expectedLikes);

        Long result = unlikeWorkUseCase.execute(WORK_ID, USER_ID);

        assertEquals(expectedLikes, result);
        verify(likePort, times(1)).unlikeWork(WORK_ID, USER_ID);
    }

    @Test
    void execute_ShouldNotGoBelowZero() {
        when(likePort.unlikeWork(WORK_ID, USER_ID)).thenReturn(0L);

        Long result = unlikeWorkUseCase.execute(WORK_ID, USER_ID);

        assertEquals(0L, result);
        verify(likePort, times(1)).unlikeWork(WORK_ID, USER_ID);
    }
    
    @Test
    void execute_ShouldUseProvidedUserId() {
        Long differentUserId = 999L;
        Long expectedLikes = 0L;
        
        when(likePort.unlikeWork(WORK_ID, differentUserId)).thenReturn(expectedLikes);

        Long result = unlikeWorkUseCase.execute(WORK_ID, differentUserId);

        verify(likePort, times(1)).unlikeWork(WORK_ID, differentUserId);
        assertEquals(expectedLikes, result);
    }
}
