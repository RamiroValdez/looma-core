package com.amool.application.usecase;

import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.usecases.GetChapterWithContent;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.ChapterContent;
import com.amool.domain.model.ChapterWithContentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    private void givenChapterExists(Long workId, Long chapterId, String title) {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setTitle(title);
        chapter.setWorkId(workId);
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.of(chapter));
    }

    private void givenChapterDoesNotExist(Long workId, Long chapterId) {
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.empty());
    }

    private void givenContentExists(Long workId, Long chapterId, String language, String content) {
        ChapterContent chapterContent = new ChapterContent(
                workId.toString(),
                chapterId.toString(),
                Map.of(language, content),
                language
        );
        when(loadChapterContentPort.loadContent(workId.toString(), chapterId.toString(), language))
                .thenReturn(Optional.of(chapterContent));
    }

    private void givenContentMissing(Long workId, Long chapterId, String language) {
        when(loadChapterContentPort.loadContent(workId.toString(), chapterId.toString(), language))
                .thenReturn(Optional.empty());
    }

    private void givenAvailableLanguages(Long workId, Long chapterId, List<String> languages) {
        when(loadChapterContentPort.getAvailableLanguages(workId.toString(), chapterId.toString()))
                .thenReturn(languages);
    }

    private Optional<ChapterWithContentResult> whenGetChapterWithContent(Long workId, Long chapterId, String language) {
        return useCase.execute(workId, chapterId, language);
    }

    private void thenResultPresent(Optional<?> result) {
        assertTrue(result.isPresent());
    }

    private void thenResultEmpty(Optional<?> result) {
        assertTrue(result.isEmpty());
    }

    private void thenChapterDataMatches(ChapterWithContentResult result, Long expectedChapterId, String expectedTitle) {
        assertEquals(expectedChapterId, result.getChapterWithContent().chapter().getId());
        assertEquals(expectedTitle, result.getChapterWithContent().chapter().getTitle());
    }

    private void thenContentEquals(ChapterWithContentResult result, String expectedContent) {
        assertEquals(expectedContent, result.getContent());
    }

    @Test
    public void when_ChapterAndContentExist_ThenReturnChapterWithContent() {
        givenChapterExists(WORK_ID, CHAPTER_ID, CHAPTER_TITLE);
        givenContentExists(WORK_ID, CHAPTER_ID, LANGUAGE, CHAPTER_CONTENT);
        givenAvailableLanguages(WORK_ID, CHAPTER_ID, List.of(LANGUAGE));

        Optional<ChapterWithContentResult> result = whenGetChapterWithContent(WORK_ID, CHAPTER_ID, LANGUAGE);

        thenResultPresent(result);
        ChapterWithContentResult chapterWithContent = result.orElseThrow();
        thenChapterDataMatches(chapterWithContent, CHAPTER_ID, CHAPTER_TITLE);
        thenContentEquals(chapterWithContent, CHAPTER_CONTENT);
    }

    @Test
    public void when_ChapterDoesNotExist_ThenReturnEmpty() {
        givenChapterDoesNotExist(WORK_ID, CHAPTER_ID);

        Optional<ChapterWithContentResult> result = whenGetChapterWithContent(WORK_ID, CHAPTER_ID, LANGUAGE);

        thenResultEmpty(result);
    }

    @Test
    public void when_ContentDoesNotExist_ThenReturnChapterWithEmptyContent() {
        givenChapterExists(WORK_ID, CHAPTER_ID, CHAPTER_TITLE);
        givenContentMissing(WORK_ID, CHAPTER_ID, LANGUAGE);
        givenAvailableLanguages(WORK_ID, CHAPTER_ID, List.of());

        Optional<ChapterWithContentResult> result = whenGetChapterWithContent(WORK_ID, CHAPTER_ID, LANGUAGE);

        thenResultPresent(result);
        ChapterWithContentResult chapterWithContent = result.orElseThrow();
        thenChapterDataMatches(chapterWithContent, CHAPTER_ID, CHAPTER_TITLE);
        thenContentEquals(chapterWithContent, "");
    }
}
