package com.amool.application.usecase;

import com.amool.application.port.out.LikePort;
import com.amool.application.usecases.UnlikeChapterUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.amool.adapters.in.rest.dtos.LikeResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnlikeChapterUseCaseTest {

    @Mock
    private LikePort likePort;

    @InjectMocks
    private UnlikeChapterUseCase unlikeChapterUseCase;

    private static final Long CHAPTER_ID = 1L;
    private static final Long INITIAL_LIKES = 5L;
    private static final Long USER_ID = 1L;

    @Test
    void execute_ShouldDecrementLikesAndReturnResponse() {
        when(likePort.unlikeChapter(CHAPTER_ID, USER_ID)).thenReturn(INITIAL_LIKES - 1);
        when(likePort.hasUserLikedChapter(CHAPTER_ID, USER_ID)).thenReturn(false);

        LikeResponseDto response = unlikeChapterUseCase.execute(CHAPTER_ID, USER_ID);

        assertEquals(CHAPTER_ID, response.getWorkId());
        assertEquals(INITIAL_LIKES - 1, response.getLikeCount());
        assertFalse(response.isLikedByUser());
        verify(likePort).unlikeChapter(CHAPTER_ID, USER_ID);
        verify(likePort).hasUserLikedChapter(CHAPTER_ID, USER_ID);
    }

    @Test
    void execute_ShouldNotGoBelowZero() {
        when(likePort.unlikeChapter(CHAPTER_ID, USER_ID)).thenReturn(0L);
        when(likePort.hasUserLikedChapter(CHAPTER_ID, USER_ID)).thenReturn(false);

        LikeResponseDto response = unlikeChapterUseCase.execute(CHAPTER_ID, USER_ID);

        assertEquals(0L, response.getLikeCount());
        assertFalse(response.isLikedByUser());
    }

    @Test
    void execute_ShouldUseProvidedUserId() {
        Long differentUserId = 999L;
        when(likePort.unlikeChapter(CHAPTER_ID, differentUserId)).thenReturn(INITIAL_LIKES - 1);
        when(likePort.hasUserLikedChapter(CHAPTER_ID, differentUserId)).thenReturn(false);

        LikeResponseDto response = unlikeChapterUseCase.execute(CHAPTER_ID, differentUserId);

        assertEquals(INITIAL_LIKES - 1, response.getLikeCount());
        assertFalse(response.isLikedByUser());
        verify(likePort).unlikeChapter(CHAPTER_ID, differentUserId);
        verify(likePort).hasUserLikedChapter(CHAPTER_ID, differentUserId);
    }
}
