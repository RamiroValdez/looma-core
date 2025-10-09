package com.amool.hexagonal.application.service;

import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.application.port.out.LoadChapterContentPort;
import com.amool.hexagonal.application.port.out.LoadChapterPort;
import com.amool.hexagonal.application.port.out.SaveChapterContentPort;
import com.amool.hexagonal.application.port.out.SaveChapterPort;
import com.amool.hexagonal.application.port.out.DeleteChapterPort;
import com.amool.hexagonal.application.port.out.DeleteChapterContentPort;
import com.amool.hexagonal.domain.model.Chapter;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChapterServiceImplUnitTest {

    @Mock
    private LoadChapterPort loadChapterPort;

    @Mock
    private LoadChapterContentPort loadChapterContentPort;

    @Mock
    private SaveChapterPort saveChapterPort;

    @Mock
    private SaveChapterContentPort saveChapterContentPort;

    @Mock
    private DeleteChapterPort deleteChapterPort;

    @Mock
    private DeleteChapterContentPort deleteChapterContentPort;

    @Mock
    private EntityManager entityManager;

    private ChapterServiceImpl chapterService;

    @Mock
    private LanguageEntity mockLanguageEntity;

    @Mock
    private Chapter mockSavedChapter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        chapterService = new ChapterServiceImpl(
            loadChapterPort,
            loadChapterContentPort,
            saveChapterPort,
            saveChapterContentPort,
            deleteChapterPort,
            deleteChapterContentPort
        );

        var entityManagerField = ChapterServiceImpl.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(chapterService, entityManager);
    }

    @Test
    void createEmptyChapter_ShouldSaveChapterInPostgreSQL() {
        Long workId = 123L;
        Long languageId = 456L;

        when(saveChapterPort.saveChapter(any(Chapter.class))).thenReturn(mockSavedChapter);
        when(mockSavedChapter.getId()).thenReturn(1L);

        Chapter result = chapterService.createEmptyChapter(workId, languageId, "TEXT");

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(saveChapterPort).saveChapter(argThat(chapter -> {
            assertEquals(workId, chapter.getWorkId());
            assertEquals(languageId, chapter.getLanguageId());
            return true;
        }));
    }

    @Test
    void createEmptyChapter_WithTextContent_ShouldSaveEmptyContentInMongoDB() {
        Long workId = 123L;
        Long languageId = 456L;

        when(saveChapterPort.saveChapter(any(Chapter.class))).thenReturn(mockSavedChapter);
        when(mockSavedChapter.getId()).thenReturn(1L);

        chapterService.createEmptyChapter(workId, languageId, "TEXT");

        verify(saveChapterContentPort).saveContent(
            eq(workId.toString()),
            eq("1"),
            anyString(),
            eq("")
        );
    }

    @Test
    void createEmptyChapter_WithImagesContent_ShouldNotSaveContentInMongoDB() {
        Long workId = 123L;
        Long languageId = 456L;

        when(saveChapterPort.saveChapter(any(Chapter.class))).thenReturn(mockSavedChapter);

        chapterService.createEmptyChapter(workId, languageId, "IMAGES");

        verify(saveChapterContentPort, never()).saveContent(
            anyString(),
            anyString(),
            anyString(),
            anyString()
        );
    }

    @Test
    void createEmptyChapter_WithNullContentType_ShouldNotSaveContentInMongoDB() {
        Long workId = 123L;
        Long languageId = 456L;

        when(saveChapterPort.saveChapter(any(Chapter.class))).thenReturn(mockSavedChapter);

        chapterService.createEmptyChapter(workId, languageId, null);

        verify(saveChapterContentPort, never()).saveContent(
            anyString(),
            anyString(),
            anyString(),
            anyString()
        );
    }
}
