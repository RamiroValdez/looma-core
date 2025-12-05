package com.amool.application.usecase;

import com.amool.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.LoadLanguagePort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.GetChapterForEdit;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.ChapterContent;
import com.amool.domain.model.Language;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class GetChapterForEditTest {

    private LoadChapterPort loadChapterPort;
    private LoadChapterContentPort loadChapterContentPort;
    private LoadLanguagePort loadLanguagePort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private GetChapterForEdit useCase;

    private static final Long CHAPTER_ID = 1L;
    private static final Long WORK_ID = 1L;
    private static final String LANGUAGE_CODE = "es";
    private static final String WORK_TITLE = "Test Work";
    private static final String CHAPTER_CONTENT = "Test content";

    @BeforeEach
    public void setUp() {
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        loadChapterContentPort = Mockito.mock(LoadChapterContentPort.class);
        loadLanguagePort = Mockito.mock(LoadLanguagePort.class);
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        useCase = new GetChapterForEdit(
            loadChapterPort,
            loadChapterContentPort,
            loadLanguagePort,
            obtainWorkByIdPort
        );
    }

    private Chapter givenChapterExists(Long chapterId, Long workId) {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(workId);
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.of(chapter));
        return chapter;
    }

    private void givenChapterDoesNotExist(Long chapterId) {
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.empty());
    }

    private Language givenWorkWithOriginalLanguage(Long workId, String workTitle, String languageCode) {
        Work work = new Work();
        work.setId(workId);
        work.setTitle(workTitle);
        Language language = new Language();
        language.setCode(languageCode);
        work.setOriginalLanguage(language);
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));
        return language;
    }

    private void givenAvailableLanguages(Long workId, Long chapterId, List<String> codes) {
        when(loadChapterContentPort.getAvailableLanguages(workId.toString(), chapterId.toString())).thenReturn(codes);
        when(loadLanguagePort.getLanguagesByCodes(anyList())).thenReturn(codes.stream().map(code -> {
            Language lang = new Language();
            lang.setCode(code);
            return lang;
        }).toList());
    }

    private void givenChapterContent(Long workId, Long chapterId, String languageCode, String content) {
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put(languageCode, content);
        ChapterContent chapterContent = new ChapterContent(workId.toString(), chapterId.toString(), contentMap, languageCode);
        when(loadChapterContentPort.loadContent(anyString(), anyString(), eq(languageCode))).thenReturn(Optional.of(chapterContent));
    }

    private Optional<ChapterResponseDto> whenGetChapterForEdit(Long chapterId, String languageCode) {
        return useCase.execute(chapterId, languageCode);
    }

    private void thenChapterDtoPresent(Optional<ChapterResponseDto> result) {
        assertTrue(result.isPresent(), "Se esperaba capítulo presente");
    }

    private void thenChapterDtoEmpty(Optional<ChapterResponseDto> result) {
        assertTrue(result.isEmpty(), "Se esperaba resultado vacío");
    }

    private void thenChapterBasics(ChapterResponseDto dto, Long expectedChapterId, String expectedWorkTitle) {
        assertEquals(expectedChapterId, dto.getId());
        assertEquals(expectedWorkTitle, dto.getWorkName());
    }

    private void thenAvailableLanguagesSize(ChapterResponseDto dto, int expectedSize) {
        assertEquals(expectedSize, dto.getAvailableLanguages().size());
    }

    private void thenDefaultLanguageCodeIs(ChapterResponseDto dto, String expectedCode) {
        assertEquals(expectedCode, dto.getLanguageDefaultCode().getCode());
    }

    @Test
    public void when_ChapterExists_ThenReturnChapterForEdit() {
        givenChapterExists(CHAPTER_ID, WORK_ID);
        Language originalLanguage = givenWorkWithOriginalLanguage(WORK_ID, WORK_TITLE, LANGUAGE_CODE);
        givenAvailableLanguages(WORK_ID, CHAPTER_ID, List.of(LANGUAGE_CODE));
        givenChapterContent(WORK_ID, CHAPTER_ID, LANGUAGE_CODE, CHAPTER_CONTENT);

        Optional<ChapterResponseDto> result = whenGetChapterForEdit(CHAPTER_ID, LANGUAGE_CODE);

        thenChapterDtoPresent(result);
        ChapterResponseDto dto = result.get();
        thenChapterBasics(dto, CHAPTER_ID, WORK_TITLE);
        thenAvailableLanguagesSize(dto, 1);
        thenDefaultLanguageCodeIs(dto, LANGUAGE_CODE);
    }

    @Test
    public void when_ChapterDoesNotExist_ThenReturnEmpty() {
        givenChapterDoesNotExist(CHAPTER_ID);

        Optional<ChapterResponseDto> result = whenGetChapterForEdit(CHAPTER_ID, LANGUAGE_CODE);

        thenChapterDtoEmpty(result);
    }
}
