package com.amool.application.usecase;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;
import com.amool.application.usecases.ToggleChapterLikeUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ToggleChapterLikeTest {

    private LikePort likePort;
    private ToggleChapterLikeUseCase useCase;

    @BeforeEach
    void setUp() {
        likePort = Mockito.mock(LikePort.class);
        useCase = new ToggleChapterLikeUseCase(likePort);
    }

    @Test
    public void when_ChapterNotLiked_ThenLikeChapter() {
        Long chapterId = 1L;
        Long userId = 100L;
        Long expectedLikeCount = 5L;
        givenChapterIsNotLikedByUser(chapterId, userId, expectedLikeCount);

        LikeResponseDto result = whenTogglingChapterLike(chapterId, userId);

        thenChapterIsLiked(result, chapterId, expectedLikeCount);
        thenLikeOperationIsPerformed(chapterId, userId);
    }

    @Test
    public void when_ChapterAlreadyLiked_ThenUnlikeChapter() {
        Long chapterId = 2L;
        Long userId = 200L;
        Long expectedLikeCount = 3L;
        givenChapterIsAlreadyLikedByUser(chapterId, userId, expectedLikeCount);

        LikeResponseDto result = whenTogglingChapterLike(chapterId, userId);

        thenChapterIsUnliked(result, chapterId, expectedLikeCount);
        thenUnlikeOperationIsPerformed(chapterId, userId);
    }

    @Test
    public void when_LikeChapterWithZeroLikes_ThenReturnCorrectCount() {
        Long chapterId = 3L;
        Long userId = 300L;
        Long expectedLikeCount = 1L;
        givenChapterIsNotLikedByUser(chapterId, userId, expectedLikeCount);

        LikeResponseDto result = whenTogglingChapterLike(chapterId, userId);

        thenLikeCountIsCorrect(result, expectedLikeCount);
        thenChapterIsMarkedAsLiked(result);
    }

    @Test
    public void when_UnlikeChapterWithOneLike_ThenReturnZeroCount() {
        Long chapterId = 4L;
        Long userId = 400L;
        Long expectedLikeCount = 0L;
        givenChapterIsAlreadyLikedByUser(chapterId, userId, expectedLikeCount);

        LikeResponseDto result = whenTogglingChapterLike(chapterId, userId);

        thenLikeCountIsCorrect(result, expectedLikeCount);
        thenChapterIsMarkedAsUnliked(result);
    }

    @Test
    public void when_ToggleLikeWithNullValues_ThenPassNullsToPort() {
        Long chapterId = null;
        Long userId = null;
        Long expectedLikeCount = 1L;
        givenChapterIsNotLikedByUser(chapterId, userId, expectedLikeCount);

        LikeResponseDto result = whenTogglingChapterLike(chapterId, userId);

        thenResultIsNotNull(result);
        thenWorkIdIsNull(result);
        thenLikeCountIsCorrect(result, expectedLikeCount);
        thenChapterIsMarkedAsLiked(result);
        thenLikeOperationIsPerformed(chapterId, userId);
    }

    private void givenChapterIsNotLikedByUser(Long chapterId, Long userId, Long expectedLikeCount) {
        when(likePort.hasUserLikedChapter(chapterId, userId)).thenReturn(false);
        when(likePort.likeChapter(chapterId, userId)).thenReturn(expectedLikeCount);
    }

    private void givenChapterIsAlreadyLikedByUser(Long chapterId, Long userId, Long expectedLikeCount) {
        when(likePort.hasUserLikedChapter(chapterId, userId)).thenReturn(true);
        when(likePort.unlikeChapter(chapterId, userId)).thenReturn(expectedLikeCount);
    }

    private LikeResponseDto whenTogglingChapterLike(Long chapterId, Long userId) {
        return useCase.execute(chapterId, userId);
    }

    private void thenChapterIsLiked(LikeResponseDto result, Long chapterId, Long expectedLikeCount) {
        assertNotNull(result);
        assertEquals(chapterId, result.getWorkId());
        assertEquals(expectedLikeCount, result.getLikeCount());
        assertTrue(result.isLikedByUser());
    }

    private void thenChapterIsUnliked(LikeResponseDto result, Long chapterId, Long expectedLikeCount) {
        assertNotNull(result);
        assertEquals(chapterId, result.getWorkId());
        assertEquals(expectedLikeCount, result.getLikeCount());
        assertFalse(result.isLikedByUser());
    }

    private void thenLikeCountIsCorrect(LikeResponseDto result, Long expectedLikeCount) {
        assertEquals(expectedLikeCount, result.getLikeCount());
    }

    private void thenChapterIsMarkedAsLiked(LikeResponseDto result) {
        assertTrue(result.isLikedByUser());
    }

    private void thenChapterIsMarkedAsUnliked(LikeResponseDto result) {
        assertFalse(result.isLikedByUser());
    }

    private void thenResultIsNotNull(LikeResponseDto result) {
        assertNotNull(result);
    }

    private void thenWorkIdIsNull(LikeResponseDto result) {
        assertNull(result.getWorkId());
    }

    private void thenLikeOperationIsPerformed(Long chapterId, Long userId) {
        verify(likePort).hasUserLikedChapter(chapterId, userId);
        verify(likePort).likeChapter(chapterId, userId);
        verify(likePort, never()).unlikeChapter(chapterId, userId);
    }

    private void thenUnlikeOperationIsPerformed(Long chapterId, Long userId) {
        verify(likePort).hasUserLikedChapter(chapterId, userId);
        verify(likePort).unlikeChapter(chapterId, userId);
        verify(likePort, never()).likeChapter(chapterId, userId);
    }
}
