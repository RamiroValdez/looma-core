package com.amool.application.usecase;

import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.usecases.GetChapterWithContent;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.ChapterContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetChapterWithContentTest {

    private LoadChapterPort loadChapterPort;
    private LoadChapterContentPort loadChapterContentPort;
    private GetChapterWithContent useCase;

    private static final Long WORK_ID = 1L;
    private static final Long CHAPTER_ID = 10L;
    private static final String LANGUAGE = "es";
    private static final String CHAPTER_TITLE = "Test Chapter";
    private static final String CHAPTER_CONTENT = "Test content";

    @BeforeEach
    public void setUp() {
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        loadChapterContentPort = Mockito.mock(LoadChapterContentPort.class);
        useCase = new GetChapterWithContent(loadChapterPort, loadChapterContentPort);
    }

    @Test
    public void when_ChapterAndContentExist_ThenReturnChapterWithContent() {
        Chapter chapter = new Chapter();
        chapter.setId(CHAPTER_ID);
        chapter.setTitle(CHAPTER_TITLE);
        chapter.setWorkId(WORK_ID);

        ChapterContent content = new ChapterContent(
            WORK_ID.toString(), 
            CHAPTER_ID.toString(), 
            Map.of(LANGUAGE, CHAPTER_CONTENT), 
            LANGUAGE
        );

        when(loadChapterPort.loadChapter(WORK_ID, CHAPTER_ID))
            .thenReturn(Optional.of(chapter));
            
        when(loadChapterContentPort.loadContent(
            WORK_ID.toString(), 
            CHAPTER_ID.toString(), 
            LANGUAGE
        )).thenReturn(Optional.of(content));

        when(loadChapterContentPort.getAvailableLanguages(
            WORK_ID.toString(),
            CHAPTER_ID.toString()
        )).thenReturn(java.util.List.of(LANGUAGE));

        Optional<com.amool.domain.model.ChapterWithContentResult> result = useCase.execute(WORK_ID, CHAPTER_ID, LANGUAGE);

        assertTrue(result.isPresent());
        com.amool.domain.model.ChapterWithContentResult chapterWithContent = result.get();
        assertEquals(CHAPTER_ID, chapterWithContent.getChapterWithContent().chapter().getId());
        assertEquals(CHAPTER_TITLE, chapterWithContent.getChapterWithContent().chapter().getTitle());
        assertEquals(CHAPTER_CONTENT, chapterWithContent.getContent());
    }

    @Test
    public void when_ChapterDoesNotExist_ThenReturnEmpty() {
        when(loadChapterPort.loadChapter(WORK_ID, CHAPTER_ID))
            .thenReturn(Optional.empty());

        Optional<com.amool.domain.model.ChapterWithContentResult> result = useCase.execute(WORK_ID, CHAPTER_ID, LANGUAGE);

        assertTrue(result.isEmpty());
    }

    @Test
    public void when_ContentDoesNotExist_ThenReturnChapterWithEmptyContent() {
        Chapter chapter = new Chapter();
        chapter.setId(CHAPTER_ID);
        chapter.setTitle(CHAPTER_TITLE);
        chapter.setWorkId(WORK_ID);

        when(loadChapterPort.loadChapter(WORK_ID, CHAPTER_ID))
            .thenReturn(Optional.of(chapter));
            
        when(loadChapterContentPort.loadContent(
            anyString(), 
            anyString(), 
            anyString()
        )).thenReturn(Optional.empty());

        when(loadChapterContentPort.getAvailableLanguages(
            anyString(),
            anyString()
        )).thenReturn(java.util.List.of());

        Optional<com.amool.domain.model.ChapterWithContentResult> result = useCase.execute(WORK_ID, CHAPTER_ID, LANGUAGE);

        assertTrue(result.isPresent());
        com.amool.domain.model.ChapterWithContentResult chapterWithContent = result.get();
        assertEquals(CHAPTER_ID, chapterWithContent.getChapterWithContent().chapter().getId());
        assertEquals(CHAPTER_TITLE, chapterWithContent.getChapterWithContent().chapter().getTitle());
        assertEquals("", chapterWithContent.getContent());
    }

}
