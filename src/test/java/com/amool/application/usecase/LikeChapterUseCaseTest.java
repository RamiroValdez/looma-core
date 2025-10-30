package com.amool.application.usecase;

import com.amool.application.port.out.LikePort;
import com.amool.application.usecases.LikeChapterUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeChapterUseCaseTest {

    @Mock
    private LikePort likePort;

    @InjectMocks
    private LikeChapterUseCase likeChapterUseCase;

    private static final Long CHAPTER_ID = 1L;
    private static final Long INITIAL_LIKES = 5L;
    private static final Long USER_ID = 1L;

    @Test
    void execute_ShouldIncrementLikesAndReturnNewCount() {
        when(likePort.likeChapter(CHAPTER_ID, USER_ID)).thenReturn(INITIAL_LIKES + 1);

        Long result = likeChapterUseCase.execute(CHAPTER_ID, USER_ID);

        assertEquals(INITIAL_LIKES + 1, result);
        verify(likePort, times(1)).likeChapter(CHAPTER_ID, USER_ID);
    }

    @Test
    void execute_ShouldReturnOneWhenNoPreviousLikes() {
        when(likePort.likeChapter(CHAPTER_ID, USER_ID)).thenReturn(1L);

        Long result = likeChapterUseCase.execute(CHAPTER_ID, USER_ID);
        
        assertEquals(1L, result);
        verify(likePort, times(1)).likeChapter(CHAPTER_ID, USER_ID);
    }
    
    @Test
    void execute_ShouldUseProvidedUserId() {
        Long differentUserId = 999L;
        Long expectedLikes = 1L;
        
        when(likePort.likeChapter(CHAPTER_ID, differentUserId)).thenReturn(expectedLikes);

        Long result = likeChapterUseCase.execute(CHAPTER_ID, differentUserId);
        
        assertEquals(expectedLikes, result);
        verify(likePort, times(1)).likeChapter(CHAPTER_ID, differentUserId);
    }
}
