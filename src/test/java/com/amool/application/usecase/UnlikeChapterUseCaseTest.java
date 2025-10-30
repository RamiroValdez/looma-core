package com.amool.application.usecase;

import com.amool.application.port.out.LikePort;
import com.amool.application.usecases.UnlikeChapterUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void execute_ShouldDecrementLikesAndReturnNewCount() {
        when(likePort.unlikeChapter(CHAPTER_ID, USER_ID)).thenReturn(INITIAL_LIKES - 1);

        Long result = unlikeChapterUseCase.execute(CHAPTER_ID, USER_ID);

        assertEquals(INITIAL_LIKES - 1, result);
        verify(likePort, times(1)).unlikeChapter(CHAPTER_ID, USER_ID);
    }

    @Test
    void execute_ShouldNotGoBelowZero() {
        when(likePort.unlikeChapter(CHAPTER_ID, USER_ID)).thenReturn(0L);

        Long result = unlikeChapterUseCase.execute(CHAPTER_ID, USER_ID);
        
        assertEquals(0L, result);
        verify(likePort, times(1)).unlikeChapter(CHAPTER_ID, USER_ID);
    }
    
    @Test
    void execute_ShouldUseProvidedUserId() {
        Long differentUserId = 999L;
        Long expectedLikes = 4L;
        
        when(likePort.unlikeChapter(CHAPTER_ID, differentUserId)).thenReturn(expectedLikes);

        Long result = unlikeChapterUseCase.execute(CHAPTER_ID, differentUserId);
        
        assertEquals(expectedLikes, result);
        verify(likePort, times(1)).unlikeChapter(CHAPTER_ID, differentUserId);
    }
}
