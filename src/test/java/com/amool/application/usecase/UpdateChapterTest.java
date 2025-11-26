package com.amool.application.usecase;

import com.amool.adapters.in.rest.dtos.UpdateChapterRequest;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.SaveChapterContentPort;
import com.amool.application.port.out.UpdateChapterPort;
import com.amool.application.usecases.UpdateChapter;
import com.amool.domain.model.Chapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UpdateChapterTest {

    private static final Long CHAPTER_ID = 1L;
    private static final Long WORK_ID = 100L;

    private LoadChapterPort loadChapterPort;
    private UpdateChapterPort updateChapterPort;
    private SaveChapterContentPort saveChapterContentPort;
    private UpdateChapter useCase;

    private Chapter existingChapter;
    private UpdateChapterRequest updateRequest;

    @BeforeEach
    public void setUp() {
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        updateChapterPort = Mockito.mock(UpdateChapterPort.class);
        saveChapterContentPort = Mockito.mock(SaveChapterContentPort.class);
        
        useCase = new UpdateChapter(
            loadChapterPort,
            updateChapterPort,
            saveChapterContentPort
        );

        existingChapter = new Chapter();
        existingChapter.setId(CHAPTER_ID);
        existingChapter.setWorkId(WORK_ID);
        existingChapter.setTitle("Original Title");
        existingChapter.setPrice(BigDecimal.valueOf(0.0));
        existingChapter.setPublicationStatus("DRAFT");
        existingChapter.setAllowAiTranslation(false);

        updateRequest = new UpdateChapterRequest();
    }

    @Test
    public void shouldUpdateTitleWhenRequestIncludesIt() {
        givenChapterLoaded();
        givenUpdateWillSucceed();
        updateRequest.setTitle("Updated Title");

        boolean result = whenUpdatingChapter();

        thenUpdateSucceeded(result);
        thenChapterUpdated(chapter -> assertEquals("Updated Title", chapter.getTitle()));
        thenNoContentSaved();
    }

    @Test
    public void shouldUpdateStatusWhenRequestIncludesIt() {
        givenChapterLoaded();
        givenUpdateWillSucceed();
        updateRequest.setStatus("PUBLISHED");

        boolean result = whenUpdatingChapter();

        thenUpdateSucceeded(result);
        thenChapterUpdated(chapter -> assertEquals("PUBLISHED", chapter.getPublicationStatus()));
        thenNoContentSaved();
    }

    @Test
    public void shouldUpdatePriceWhenRequestIncludesIt() {
        givenChapterLoaded();
        givenUpdateWillSucceed();
        updateRequest.setPrice(BigDecimal.valueOf(2.99));

        boolean result = whenUpdatingChapter();

        thenUpdateSucceeded(result);
        thenChapterUpdated(chapter -> assertEquals(BigDecimal.valueOf(2.99), chapter.getPrice()));
        thenNoContentSaved();
    }

    @Test
    public void shouldUpdateAiTranslationFlag() {
        givenChapterLoaded();
        givenUpdateWillSucceed();
        updateRequest.setAllow_ai_translation(true);

        boolean result = whenUpdatingChapter();

        thenUpdateSucceeded(result);
        thenChapterUpdated(chapter -> assertTrue(chapter.getAllowAiTranslation()));
        thenNoContentSaved();
    }

    @Test
    public void shouldSaveContentForEachVersionProvided() {
        givenChapterLoaded();
        givenUpdateWillSucceed();
        Map<String, String> versions = Map.of(
            "en", "English content",
            "es", "Contenido en español"
        );
        updateRequest.setVersions(versions);

        boolean result = whenUpdatingChapter();

        thenUpdateSucceeded(result);
        thenChapterUpdated(chapter -> assertEquals(versions, updateRequest.getVersions()));
        thenContentSavedForVersions(versions);
    }

    @Test
    public void shouldReturnFalseWhenChapterNotFound() {
        givenChapterNotFound();

        boolean result = whenUpdatingChapter();

        thenUpdateFailed(result);
        thenChapterNotUpdated();
        thenNoContentSaved();
    }

    @Test
    public void shouldReturnFalseWhenUpdateFails() {
        givenChapterLoaded();
        givenUpdateWillFail();
        updateRequest.setTitle("New Title");

        boolean result = whenUpdatingChapter();

        thenUpdateFailed(result);
        thenChapterUpdatedAttempted();
        thenNoContentSaved();
    }

    private void givenChapterLoaded() {
        when(loadChapterPort.loadChapterForEdit(CHAPTER_ID)).thenReturn(Optional.of(existingChapter));
    }

    private void givenChapterNotFound() {
        when(loadChapterPort.loadChapterForEdit(CHAPTER_ID)).thenReturn(Optional.empty());
    }

    private void givenUpdateWillSucceed() {
        when(updateChapterPort.updateChapter(any(Chapter.class))).thenReturn(Optional.of(existingChapter));
    }

    private void givenUpdateWillFail() {
        when(updateChapterPort.updateChapter(any(Chapter.class))).thenReturn(Optional.empty());
    }

    private boolean whenUpdatingChapter() {
        return useCase.execute(CHAPTER_ID, updateRequest);
    }

    private void thenUpdateSucceeded(boolean result) {
        assertTrue(result);
    }

    private void thenUpdateFailed(boolean result) {
        assertFalse(result);
    }

    private void thenChapterUpdated(java.util.function.Consumer<Chapter> assertions) {
        verify(updateChapterPort).updateChapter(argThat(chapter -> {
            assertions.accept(chapter);
            return true;
        }));
    }

    private void thenChapterUpdatedAttempted() {
        verify(updateChapterPort).updateChapter(any(Chapter.class));
    }

    private void thenChapterNotUpdated() {
        verify(updateChapterPort, never()).updateChapter(any());
    }

    private void thenNoContentSaved() {
        verify(saveChapterContentPort, never()).saveContent(any(), any(), any(), any());
    }

    private void thenContentSavedForVersions(Map<String, String> versions) {
        versions.forEach((language, content) ->
            verify(saveChapterContentPort).saveContent(
                eq(WORK_ID.toString()),
                eq(CHAPTER_ID.toString()),
                eq(language),
                eq(content)
            )
        );
    }
}
