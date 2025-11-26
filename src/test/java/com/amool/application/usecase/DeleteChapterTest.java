package com.amool.application.usecase;

import com.amool.application.port.out.*;
import com.amool.application.usecases.DeleteChapter;
import com.amool.domain.model.Chapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DeleteChapterTest {

    private LoadChapterPort loadChapterPort;
    private DeleteChapterContentPort deleteChapterContentPort;
    private DeleteChapterPort deleteChapterPort;
    private DeleteChapter useCase;
    private LoadWorkOwnershipPort loadWorkOwnershipPort;

    @BeforeEach
    void setUp() {
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        deleteChapterContentPort = Mockito.mock(DeleteChapterContentPort.class);
        deleteChapterPort = Mockito.mock(DeleteChapterPort.class);
        loadWorkOwnershipPort = Mockito.mock(LoadWorkOwnershipPort.class);
        useCase = new DeleteChapter(
            loadChapterPort,
            deleteChapterContentPort,
            deleteChapterPort,
            loadWorkOwnershipPort
        );
    }

    private void givenOwner(Long workId, Long userId) {
        when(loadWorkOwnershipPort.isOwner(workId, userId)).thenReturn(true);
    }

    private void givenChapterDraft(Long workId, Long chapterId) {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(workId);
        chapter.setPublicationStatus("DRAFT");
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.of(chapter));
    }

    private void givenChapterPublished(Long workId, Long chapterId) {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(workId);
        chapter.setPublicationStatus("PUBLISHED");
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.of(chapter));
    }

    private void givenChapterNotFound(Long workId, Long chapterId) {
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.empty());
    }

    private void whenDeleteChapter(Long workId, Long chapterId, Long userId) {
        useCase.execute(workId, chapterId, userId);
    }

    private void thenDraftDeleted(Long workId, Long chapterId, Long userId) {
        verify(loadWorkOwnershipPort).isOwner(workId, userId);
        verify(loadChapterPort).loadChapter(workId, chapterId);
        verify(deleteChapterContentPort).deleteContent(workId.toString(), chapterId.toString());
        verify(deleteChapterPort).deleteChapter(workId, chapterId);
    }

    private void thenChapterNotDeleted(Long workId, Long chapterId, Long userId) {
        verify(loadWorkOwnershipPort).isOwner(workId, userId);
        verify(loadChapterPort).loadChapter(workId, chapterId);
        verify(deleteChapterContentPort, never()).deleteContent(any(), any());
        verify(deleteChapterPort, never()).deleteChapter(anyLong(), anyLong());
    }

    private void thenThrows(Class<? extends Throwable> expected, Runnable action) {
        assertThrows(expected, action::run);
    }

    @Test
    public void when_DeleteDraftChapter_ThenDeleteSuccessfully() {
        Long workId = 1L; Long chapterId = 10L; Long userId = 1L;
        givenOwner(workId, userId);
        givenChapterDraft(workId, chapterId);

        whenDeleteChapter(workId, chapterId, userId);

        thenDraftDeleted(workId, chapterId, userId);
    }

    @Test
    public void when_ChapterNotFound_ThenThrowException() {
        Long workId = 1L; Long chapterId = 99L; Long userId = 1L;
        givenOwner(workId, userId);
        givenChapterNotFound(workId, chapterId);

        thenThrows(NoSuchElementException.class, () -> whenDeleteChapter(workId, chapterId, userId));
        thenChapterNotDeleted(workId, chapterId, userId);
    }

    @Test
    public void when_DeletePublishedChapter_ThenThrowException() {
        Long workId = 1L; Long chapterId = 20L; Long userId = 1L;
        givenOwner(workId, userId);
        givenChapterPublished(workId, chapterId);

        thenThrows(IllegalStateException.class, () -> whenDeleteChapter(workId, chapterId, userId));
        thenChapterNotDeleted(workId, chapterId, userId);
    }
}
