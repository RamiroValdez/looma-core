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

    private static final Long WORK_ID = 1L;
    private static final int INITIAL_LIKES = 5;
    private static final Long USER_ID = 1L;

    @Test
    void execute_ShouldIncrementLikesAndReturnNewCount() {
        when(likePort.likeWork(WORK_ID, USER_ID)).thenReturn(INITIAL_LIKES + 1);

        int result = likeWorkUseCase.execute(WORK_ID, USER_ID);

        assertEquals(INITIAL_LIKES + 1, result);
        verify(likePort, times(1)).likeWork(WORK_ID, USER_ID);
    }

    @Test
    void execute_ShouldReturnOneWhenNoPreviousLikes() {
        when(likePort.likeWork(WORK_ID, USER_ID)).thenReturn(1);

        int result = likeWorkUseCase.execute(WORK_ID, USER_ID);
        
        assertEquals(1, result);
        verify(likePort, times(1)).likeWork(WORK_ID, USER_ID);
    }
    
    @Test
    void execute_ShouldUseProvidedUserId() {
        Long differentUserId = 999L;
        int expectedLikes = 1;
        
        when(likePort.likeWork(WORK_ID, differentUserId)).thenReturn(expectedLikes);

        int result = likeWorkUseCase.execute(WORK_ID, differentUserId);

        verify(likePort, times(1)).likeWork(WORK_ID, differentUserId);
        assertEquals(expectedLikes, result);
    }
}
