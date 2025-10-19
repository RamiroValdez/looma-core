package com.amool.application.usecase;

import com.amool.application.port.out.LoadLanguagePort;
import com.amool.application.port.out.SaveChapterContentPort;
import com.amool.application.port.out.SaveChapterPort;
import com.amool.application.usecases.CreateEmptyChapterUseCase;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.ChapterContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

public class CreateEmptyChapterUseCaseTest {

    private LoadLanguagePort loadLanguagePort;
    private SaveChapterPort saveChapterPort;
    private SaveChapterContentPort saveChapterContentPort;
    private CreateEmptyChapterUseCase useCase;

    @BeforeEach
    public void setUp() {
        loadLanguagePort = Mockito.mock(LoadLanguagePort.class);
        saveChapterPort = Mockito.mock(SaveChapterPort.class);
        saveChapterContentPort = Mockito.mock(SaveChapterContentPort.class);
        useCase = new CreateEmptyChapterUseCase(
                loadLanguagePort,
                saveChapterPort,
                saveChapterContentPort
        );
    }

    @Test
    public void when_contentTypeIsTEXT_ThenCreateChapterWithContent() {

        Long workId = 1L;
        Long languageId = 1L;
        String contentType = "TEXT";

        Chapter chapter = new Chapter();

        chapter.setId(10L);
        chapter.setWorkId(workId);
        chapter.setLanguageId(languageId);

        ChapterContent chapterContent = new ChapterContent(workId.toString(), chapter.getId().toString(), Map.of("es", ""), "es");

        Mockito.when(saveChapterPort.saveChapter(Mockito.any(Chapter.class)))
                .thenReturn(chapter);

        Mockito.when(saveChapterContentPort.saveContent(anyString(),anyString(),anyString(),anyString()))
                .thenReturn(chapterContent);

        Chapter result = useCase.execute(workId, languageId, contentType);

        assertEquals(chapter.getId(), result.getId());

        Mockito.verify(loadLanguagePort, Mockito.times(1)).loadLanguageById(languageId);
        Mockito.verify(saveChapterContentPort, Mockito.times(1)).saveContent(
                workId.toString(),
                chapter.getId().toString(),
                "es",
                ""
        );
    }

    @Test
    public void when_contentTypeIsNotTEXT_ThenCreateChapterWithoutContent() {
        Long workId = 1L;
        Long languageId = 1L;

        Chapter chapter = new Chapter();

        chapter.setId(10L);
        chapter.setWorkId(workId);
        chapter.setLanguageId(languageId);

        Mockito.when(saveChapterPort.saveChapter(Mockito.any(Chapter.class)))
                .thenReturn(chapter);

        assertEquals(chapter.getId(), useCase.execute(workId, languageId, "JSON").getId());

        Mockito.verify(loadLanguagePort, Mockito.never()).loadLanguageById(languageId);
        Mockito.verify(saveChapterContentPort, Mockito.never())
                .saveContent(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

}
