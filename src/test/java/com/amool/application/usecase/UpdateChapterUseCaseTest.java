package com.amool.application.usecase;

import com.amool.adapters.in.rest.dtos.UpdateChapterRequest;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.SaveChapterContentPort;
import com.amool.application.port.out.UpdateChapterPort;
import com.amool.application.usecases.UpdateChapterUseCase;
import com.amool.domain.model.Chapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UpdateChapterUseCaseTest {

    private LoadChapterPort loadChapterPort;
    private UpdateChapterPort updateChapterPort;
    private SaveChapterContentPort saveChapterContentPort;
    private UpdateChapterUseCase useCase;

    private final Long chapterId = 1L;
    private Chapter existingChapter;
    private UpdateChapterRequest updateRequest;

    @BeforeEach
    public void setUp() {
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        updateChapterPort = Mockito.mock(UpdateChapterPort.class);
        saveChapterContentPort = Mockito.mock(SaveChapterContentPort.class);
        
        useCase = new UpdateChapterUseCase(
            loadChapterPort,
            updateChapterPort,
            saveChapterContentPort
        );

        existingChapter = new Chapter();
        existingChapter.setId(chapterId);
        existingChapter.setWorkId(100L);
        existingChapter.setTitle("Original Title");
        existingChapter.setPrice(0.0);
        existingChapter.setPublicationStatus("DRAFT");
        existingChapter.setAllowAiTranslation(false);

        updateRequest = new UpdateChapterRequest();
    }

    @Test
    public void when_UpdateTitle_ThenUpdateSuccessfully() {
        updateRequest.setTitle("Updated Title");
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.of(existingChapter));
        when(updateChapterPort.updateChapter(any(Chapter.class))).thenReturn(Optional.of(existingChapter));

        boolean result = useCase.execute(chapterId, updateRequest);

        assertTrue(result);
        verify(updateChapterPort).updateChapter(argThat(chapter -> 
            "Updated Title".equals(chapter.getTitle())
        ));
    }

    @Test
    public void when_UpdateStatus_ThenUpdateSuccessfully() {
        updateRequest.setStatus("PUBLISHED");
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.of(existingChapter));
        when(updateChapterPort.updateChapter(any(Chapter.class))).thenReturn(Optional.of(existingChapter));

        boolean result = useCase.execute(chapterId, updateRequest);

        assertTrue(result);
        verify(updateChapterPort).updateChapter(argThat(chapter -> 
            "PUBLISHED".equals(chapter.getPublicationStatus())
        ));
    }

    @Test
    public void when_UpdatePrice_ThenUpdateSuccessfully() {
        updateRequest.setPrice(2.99);
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.of(existingChapter));
        when(updateChapterPort.updateChapter(any(Chapter.class))).thenReturn(Optional.of(existingChapter));

        boolean result = useCase.execute(chapterId, updateRequest);

        assertTrue(result);
        verify(updateChapterPort).updateChapter(argThat(chapter -> 
            2.99 == chapter.getPrice()
        ));
    }

    @Test
    public void when_UpdateAiTranslation_ThenUpdateSuccessfully() {
        updateRequest.setAllow_ai_translation(true);
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.of(existingChapter));
        when(updateChapterPort.updateChapter(any(Chapter.class))).thenReturn(Optional.of(existingChapter));

        boolean result = useCase.execute(chapterId, updateRequest);

        assertTrue(result);
        verify(updateChapterPort).updateChapter(argThat(chapter -> 
            Boolean.TRUE.equals(chapter.getAllowAiTranslation())
        ));
    }

    @Test
    public void when_UpdateVersions_ThenSaveContentForEachVersion() {
        updateRequest.setVersions(Map.of(
            "en", "English content",
            "es", "Contenido en español"
        ));
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.of(existingChapter));
        when(updateChapterPort.updateChapter(any(Chapter.class))).thenReturn(Optional.of(existingChapter));

        boolean result = useCase.execute(chapterId, updateRequest);

        assertTrue(result);
        verify(saveChapterContentPort).saveContent(
            eq("100"), eq("1"), eq("en"), eq("English content")
        );
        verify(saveChapterContentPort).saveContent(
            eq("100"), eq("1"), eq("es"), eq("Contenido en español")
        );
    }

    @Test
    public void when_ChapterNotFound_ThenReturnFalse() {
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.empty());

        boolean result = useCase.execute(chapterId, updateRequest);

        assertFalse(result);
        verify(updateChapterPort, never()).updateChapter(any());
        verify(saveChapterContentPort, never()).saveContent(any(), any(), any(), any());
    }

    @Test
    public void when_UpdateFails_ThenReturnFalse() {
        updateRequest.setTitle("New Title");
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.of(existingChapter));
        when(updateChapterPort.updateChapter(any(Chapter.class))).thenReturn(Optional.empty());

        boolean result = useCase.execute(chapterId, updateRequest);

        assertFalse(result);
        verify(updateChapterPort).updateChapter(any());
        verify(saveChapterContentPort, never()).saveContent(any(), any(), any(), any());
    }

}
