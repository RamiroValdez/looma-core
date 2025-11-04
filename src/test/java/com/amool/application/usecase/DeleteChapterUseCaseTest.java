package com.amool.application.usecase;

import com.amool.application.port.out.*;
import com.amool.application.usecases.DeleteChapterUseCase;
import com.amool.domain.model.Chapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DeleteChapterUseCaseTest {

    private LoadChapterPort loadChapterPort;
    private DeleteChapterContentPort deleteChapterContentPort;
    private DeleteChapterPort deleteChapterPort;
    private DeleteChapterUseCase useCase;
    private LoadWorkOwnershipPort loadWorkOwnershipPort;

    @BeforeEach
    void setUp() {
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        deleteChapterContentPort = Mockito.mock(DeleteChapterContentPort.class);
        deleteChapterPort = Mockito.mock(DeleteChapterPort.class);
        loadWorkOwnershipPort = Mockito.mock(LoadWorkOwnershipPort.class);
        useCase = new DeleteChapterUseCase(
            loadChapterPort,
            deleteChapterContentPort,
            deleteChapterPort,
            loadWorkOwnershipPort
        );
    }

    @Test
    public void when_DeleteDraftChapter_ThenDeleteSuccessfully() {
        Long workId = 1L;
        Long chapterId = 10L;
        Long userId = 1L;
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(workId);
        chapter.setPublicationStatus("DRAFT");
        
        when(loadWorkOwnershipPort.isOwner(workId, userId)).thenReturn(true);
        when(loadChapterPort.loadChapter(workId, chapterId))
            .thenReturn(Optional.of(chapter));
        
        useCase.execute(workId, chapterId,userId);
        
        verify(loadWorkOwnershipPort).isOwner(workId, userId);
        verify(loadChapterPort).loadChapter(workId, chapterId);
        verify(deleteChapterContentPort).deleteContent(workId.toString(), chapterId.toString());
        verify(deleteChapterPort).deleteChapter(workId, chapterId);
    }
    
    @Test
    public void when_ChapterNotFound_ThenThrowException() {
        Long workId = 1L;
        Long chapterId = 99L;
        Long userId = 1L;

        when(loadWorkOwnershipPort.isOwner(workId, userId)).thenReturn(true);
        when(loadChapterPort.loadChapter(workId, chapterId))
            .thenReturn(Optional.empty());
        
        assertThrows(NoSuchElementException.class, () -> 
            useCase.execute(workId, chapterId, userId)
        );
        
        verify(loadWorkOwnershipPort).isOwner(workId, userId);
        verify(deleteChapterContentPort, never()).deleteContent(any(), any());
        verify(deleteChapterPort, never()).deleteChapter(anyLong(), anyLong());
    }
    
    @Test
    public void when_DeletePublishedChapter_ThenThrowException() {
        Long workId = 1L;
        Long chapterId = 20L;
        Long userId = 1L;
        Chapter publishedChapter = new Chapter();
        publishedChapter.setId(chapterId);
        publishedChapter.setWorkId(workId);
        publishedChapter.setPublicationStatus("PUBLISHED");
        
        when(loadWorkOwnershipPort.isOwner(workId, userId)).thenReturn(true);
        when(loadChapterPort.loadChapter(workId, chapterId))
            .thenReturn(Optional.of(publishedChapter));
        
        assertThrows(IllegalStateException.class, () -> 
            useCase.execute(workId, chapterId,userId)
        );
        
        verify(loadWorkOwnershipPort).isOwner(workId, userId);
        verify(deleteChapterContentPort, never()).deleteContent(any(), any());
        verify(deleteChapterPort, never()).deleteChapter(anyLong(), anyLong());
    }
}
