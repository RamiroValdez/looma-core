package com.amool.application.usecase;

import com.amool.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.LoadLanguagePort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.usecases.GetChapterForEditUseCase;
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

public class GetChapterForEditUseCaseTest {

    private LoadChapterPort loadChapterPort;
    private LoadChapterContentPort loadChapterContentPort;
    private LoadLanguagePort loadLanguagePort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private GetChapterForEditUseCase useCase;

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
        
        useCase = new GetChapterForEditUseCase(
            loadChapterPort,
            loadChapterContentPort,
            loadLanguagePort,
            obtainWorkByIdPort
        );
    }

    @Test
    public void when_ChapterExists_ThenReturnChapterForEdit() {
        Chapter chapter = new Chapter();
        chapter.setId(CHAPTER_ID);
        chapter.setWorkId(WORK_ID);

        Work work = new Work();
        work.setId(WORK_ID);
        work.setTitle(WORK_TITLE);
        Language originalLanguage = new Language();
        originalLanguage.setCode(LANGUAGE_CODE);
        work.setOriginalLanguage(originalLanguage);

        when(loadChapterPort.loadChapterForEdit(CHAPTER_ID))
            .thenReturn(Optional.of(chapter));

        when(loadChapterContentPort.getAvailableLanguages(WORK_ID.toString(), CHAPTER_ID.toString()))
            .thenReturn(List.of(LANGUAGE_CODE));

        when(loadLanguagePort.getLanguagesByCodes(anyList()))
            .thenReturn(List.of(originalLanguage));

        Map<String, String> contentMap = new HashMap<>();
        contentMap.put(LANGUAGE_CODE, CHAPTER_CONTENT);
        ChapterContent chapterContent = new ChapterContent(WORK_ID.toString(), CHAPTER_ID.toString(), contentMap, LANGUAGE_CODE);
        when(loadChapterContentPort.loadContent(anyString(), anyString(), eq(LANGUAGE_CODE)))
            .thenReturn(Optional.of(chapterContent));

        when(obtainWorkByIdPort.obtainWorkById(WORK_ID))
            .thenReturn(Optional.of(work));

        Optional<ChapterResponseDto> result = useCase.execute(CHAPTER_ID, LANGUAGE_CODE);

        assertTrue(result.isPresent());
        ChapterResponseDto dto = result.get();
        assertEquals(CHAPTER_ID, dto.getId());
        assertEquals(WORK_TITLE, dto.getWorkName());
        assertEquals(1, dto.getAvailableLanguages().size());
        assertEquals(LANGUAGE_CODE, dto.getLanguageDefaultCode().getCode());
    }

    @Test
    public void when_ChapterDoesNotExist_ThenReturnEmpty() {
        when(loadChapterPort.loadChapterForEdit(CHAPTER_ID))
            .thenReturn(Optional.empty());

        Optional<ChapterResponseDto> result = useCase.execute(CHAPTER_ID, LANGUAGE_CODE);

        assertTrue(result.isEmpty());
    }

   
}
