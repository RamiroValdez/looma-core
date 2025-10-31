package com.amool.application.usecase;

import com.amool.application.port.out.LikePort;
import com.amool.application.usecases.LikeWorkUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.amool.adapters.in.rest.dtos.LikeResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeWorkUseCaseTest {

    @Mock
    private LikePort likePort;

    @InjectMocks
    private LikeWorkUseCase likeWorkUseCase;

    private static final Long WORK_ID = 1L;
    private static final Long INITIAL_LIKES = 5L;
    private static final Long USER_ID = 1L;

    @Test
    void execute_ShouldIncrementLikesAndReturnResponse() {
        when(likePort.likeWork(WORK_ID, USER_ID)).thenReturn(INITIAL_LIKES + 1);
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID)).thenReturn(true);

        LikeResponseDto response = likeWorkUseCase.execute(WORK_ID, USER_ID);

        assertEquals(WORK_ID, response.getWorkId());
        assertEquals(INITIAL_LIKES + 1, response.getLikeCount());
        assertTrue(response.isLikedByUser());
        verify(likePort).likeWork(WORK_ID, USER_ID);
        verify(likePort).hasUserLikedWork(WORK_ID, USER_ID);
    }

    @Test
    void execute_ShouldHandleFirstLike() {
        when(likePort.likeWork(WORK_ID, USER_ID)).thenReturn(1L);
        when(likePort.hasUserLikedWork(WORK_ID, USER_ID)).thenReturn(true);

        LikeResponseDto response = likeWorkUseCase.execute(WORK_ID, USER_ID);
        assertEquals(1L, response.getLikeCount());
        assertTrue(response.isLikedByUser());
    }

    @Test
    void execute_ShouldUseProvidedUserId() {
        Long differentUserId = 999L;
        when(likePort.likeWork(WORK_ID, differentUserId)).thenReturn(INITIAL_LIKES + 1);
        when(likePort.hasUserLikedWork(WORK_ID, differentUserId)).thenReturn(true);


        LikeResponseDto response = likeWorkUseCase.execute(WORK_ID, differentUserId);

        assertEquals(INITIAL_LIKES + 1, response.getLikeCount());
        assertTrue(response.isLikedByUser());
        verify(likePort).likeWork(WORK_ID, differentUserId);
        verify(likePort).hasUserLikedWork(WORK_ID, differentUserId);
    }
}
