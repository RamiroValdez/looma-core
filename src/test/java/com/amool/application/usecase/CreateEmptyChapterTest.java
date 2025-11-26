package com.amool.application.usecase;

import com.amool.application.port.out.LoadLanguagePort;
import com.amool.application.port.out.SaveChapterContentPort;
import com.amool.application.port.out.SaveChapterPort;
import com.amool.application.usecases.CreateEmptyChapter;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.ChapterContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CreateEmptyChapterTest {

    private LoadLanguagePort loadLanguagePort;
    private SaveChapterPort saveChapterPort;
    private SaveChapterContentPort saveChapterContentPort;
    private CreateEmptyChapter useCase;

    private static final Long WORK_ID = 1L;
    private static final Long LANGUAGE_ID = 1L;
    private static final String LANG_CODE = "es";

    @BeforeEach
    public void setUp() {
        loadLanguagePort = mock(LoadLanguagePort.class);
        saveChapterPort = mock(SaveChapterPort.class);
        saveChapterContentPort = mock(SaveChapterContentPort.class);
        useCase = new CreateEmptyChapter(
                loadLanguagePort,
                saveChapterPort,
                saveChapterContentPort
        );
    }

    private Chapter givenChapterPersistWillReturn(Long chapterId) {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(WORK_ID);
        chapter.setLanguageId(LANGUAGE_ID);
        when(saveChapterPort.saveChapter(any(Chapter.class))).thenReturn(chapter);
        return chapter;
    }

    private ChapterContent givenChapterContentPersistWillReturn(Long chapterId) {
        ChapterContent content = new ChapterContent(WORK_ID.toString(), chapterId.toString(), Map.of(LANG_CODE, ""), LANG_CODE);
        when(saveChapterContentPort.saveContent(anyString(), anyString(), anyString(), anyString())).thenReturn(content);
        return content;
    }

    private Chapter whenCreateEmptyChapter(String contentType) {
        return useCase.execute(WORK_ID, LANGUAGE_ID, contentType);
    }

    private void thenChapterIdMatches(Chapter created, Long expectedId) {
        assertEquals(expectedId, created.getId());
    }

    private void thenLanguageLoaded(Long languageId) {
        verify(loadLanguagePort, times(1)).loadLanguageById(languageId);
    }

    private void thenLanguageNotLoaded(Long languageId) {
        verify(loadLanguagePort, never()).loadLanguageById(languageId);
    }

    private void thenContentSaved(Long workId, Long chapterId, String langCode) {
        verify(saveChapterContentPort, times(1))
                .saveContent(workId.toString(), chapterId.toString(), langCode, "");
    }

    private void thenContentNotSaved() {
        verify(saveChapterContentPort, never()).saveContent(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void when_contentTypeIsTEXT_ThenCreateChapterWithContent() {
        Chapter persistedChapter = givenChapterPersistWillReturn(10L);
        givenChapterContentPersistWillReturn(persistedChapter.getId());

        Chapter result = whenCreateEmptyChapter("TEXT");

        thenChapterIdMatches(result, persistedChapter.getId());
        thenLanguageLoaded(LANGUAGE_ID);
        thenContentSaved(WORK_ID, persistedChapter.getId(), LANG_CODE);
    }

    @Test
    public void when_contentTypeIsNotTEXT_ThenCreateChapterWithoutContent() {
        Chapter persistedChapter = givenChapterPersistWillReturn(10L);

        Chapter result = whenCreateEmptyChapter("JSON");

        thenChapterIdMatches(result, persistedChapter.getId());
        thenLanguageNotLoaded(LANGUAGE_ID);
        thenContentNotSaved();
    }
}
