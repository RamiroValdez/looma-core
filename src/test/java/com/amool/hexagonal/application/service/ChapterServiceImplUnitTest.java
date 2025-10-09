package com.amool.hexagonal.application.service;

import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.hexagonal.application.port.out.LoadChapterContentPort;
import com.amool.hexagonal.application.port.out.LoadChapterPort;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.application.port.out.SaveChapterContentPort;
import com.amool.hexagonal.application.port.out.SaveChapterPort;
import com.amool.hexagonal.application.port.out.DeleteChapterPort;
import com.amool.hexagonal.application.port.out.DeleteChapterContentPort;
import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.domain.model.ChapterContent;
import com.amool.hexagonal.domain.model.Work;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private ObtainWorkByIdPort obtainWorkByIdPort;

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
            deleteChapterContentPort,
            obtainWorkByIdPort
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

    @Test
    void getChapterForEdit_ShouldReturnDtoWithContent() {
        Long chapterId = 1L;
        Long workId = 10L;
        Long languageId = 20L;

        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(workId);
        chapter.setLanguageId(languageId);
        chapter.setTitle("Capítulo de prueba");
        chapter.setPrice(5.5);

        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.of(chapter));

        when(entityManager.find(LanguageEntity.class, languageId)).thenReturn(mockLanguageEntity);
        when(mockLanguageEntity.getName()).thenReturn("Spanish");

        ChapterContent chapterContent = new ChapterContent(
            workId.toString(),
            chapterId.toString(),
            Map.of("es", "Contenido ES"),
            "es"
        );

        when(loadChapterContentPort.loadContent(workId.toString(), chapterId.toString(), "es"))
            .thenReturn(Optional.of(chapterContent));
        when(loadChapterContentPort.getAvailableLanguages(workId.toString(), chapterId.toString()))
            .thenReturn(List.of("es", "en"));

        Work work = new Work();
        work.setTitle("Obra prueba");
        work.setChapters(List.of(chapter));
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));

        Optional<ChapterResponseDto> result = chapterService.getChapterForEdit(chapterId, null);

        assertTrue(result.isPresent());
        ChapterResponseDto dto = result.get();
        assertEquals(chapterId, dto.getId());
        assertEquals("Capítulo de prueba", dto.getTitle());
        assertEquals("Contenido ES", dto.getContent());
        assertEquals("es", dto.getLanguageCode());
        assertEquals(List.of("es", "en"), dto.getAvailableLanguages());
        assertEquals("Obra prueba", dto.getWorkName());
        assertEquals(1, dto.getChapterNumber());
    }

    @Test
    void getChapterForEdit_ShouldReturnEmptyWhenChapterNotFound() {
        Long chapterId = 999L;
        when(loadChapterPort.loadChapterForEdit(chapterId)).thenReturn(Optional.empty());

        Optional<ChapterResponseDto> result = chapterService.getChapterForEdit(chapterId, null);

        assertTrue(result.isEmpty());
        verify(loadChapterContentPort, never()).loadContent(anyString(), anyString(), anyString());
    }
}
