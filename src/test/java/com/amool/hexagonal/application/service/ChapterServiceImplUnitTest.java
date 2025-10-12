package com.amool.hexagonal.application.service;

import com.amool.hexagonal.adapters.in.rest.dtos.LanguageDto;
import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.hexagonal.application.port.out.*;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.Instant;
import java.util.NoSuchElementException;

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
    private LoadLanguagePort loadLanguagePort;

    @Mock
    private DeleteChapterPort deleteChapterPort;

    @Mock
    private DeleteChapterContentPort deleteChapterContentPort;

    @Mock
    private UpdateChapterStatusPort updateChapterStatusPort;

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
            obtainWorkByIdPort,
            loadLanguagePort,
            updateChapterStatusPort
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

        when(loadChapterContentPort.getAvailableLanguages(workId.toString(), chapterId.toString()))
                .thenReturn(List.of("es", "en"));

        com.amool.hexagonal.domain.model.Language langEs = new com.amool.hexagonal.domain.model.Language();
        langEs.setId(1L);
        langEs.setCode("es");
        langEs.setName("Español");
        com.amool.hexagonal.domain.model.Language langEn = new com.amool.hexagonal.domain.model.Language();
        langEn.setId(2L);
        langEn.setCode("en");
        langEn.setName("Inglés");
        when(loadLanguagePort.getLanguagesByCodes(List.of("es", "en")))
                .thenReturn(List.of(langEs, langEn));

        when(loadChapterContentPort.loadContent(eq(workId.toString()), eq(chapterId.toString()), any()))
                .thenReturn(Optional.of(new ChapterContent(
                        workId.toString(),
                        chapterId.toString(),
                        Map.of("es", "Contenido ES"),
                        "es"
                )));

        Work work = new Work();
        work.setTitle("Obra prueba");
        work.setChapters(List.of(chapter));
        work.setOriginalLanguage(langEs);
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));

        Optional<ChapterResponseDto> result = chapterService.getChapterForEdit(chapterId, "es");

        assertTrue(result.isPresent());
        ChapterResponseDto dto = result.get();
        assertEquals(chapterId, dto.getId());
        assertEquals("Capítulo de prueba", dto.getTitle());
        assertEquals("Contenido ES", dto.getContent());
        assertEquals("es", dto.getLanguageDefaultCode().getCode());
        assertEquals(List.of("es", "en"), dto.getAvailableLanguages().stream().map(LanguageDto::getCode).toList());
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

    @Test
    void schedulePublication_ShouldThrow_WhenUserNotAuthenticated() {
        assertThrows(SecurityException.class, () ->
                chapterService.schedulePublication(1L, 2L, Instant.now().plusSeconds(3600), null)
        );
        verifyNoInteractions(obtainWorkByIdPort, loadChapterPort, updateChapterStatusPort);
    }

    @Test
    void schedulePublication_ShouldThrow_WhenWhenInPast() {
        assertThrows(IllegalArgumentException.class, () ->
                chapterService.schedulePublication(1L, 2L, Instant.now().minusSeconds(10), 10L)
        );
        verifyNoInteractions(obtainWorkByIdPort, loadChapterPort, updateChapterStatusPort);
    }

    @Test
    void schedulePublication_ShouldThrow_WhenWorkNotFound() {
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () ->
                chapterService.schedulePublication(1L, 2L, Instant.now().plusSeconds(3600), 10L)
        );
    }

    @Test
    void schedulePublication_ShouldThrow_WhenNotOwner() {
        var work = new Work();
        var creator = new com.amool.hexagonal.domain.model.User();
        creator.setId(999L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));

        assertThrows(SecurityException.class, () ->
                chapterService.schedulePublication(1L, 2L, Instant.now().plusSeconds(3600), 10L)
        );
    }

    @Test
    void schedulePublication_ShouldThrow_WhenChapterNotFound() {
        var work = new Work();
        var creator = new com.amool.hexagonal.domain.model.User();
        creator.setId(10L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        when(loadChapterPort.loadChapter(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                chapterService.schedulePublication(1L, 2L, Instant.now().plusSeconds(3600), 10L)
        );
        verify(updateChapterStatusPort, never()).schedulePublication(anyLong(), anyLong(), any());
    }

    @Test
    void schedulePublication_ShouldCallPort_WhenOk() {
        var work = new Work();
        var creator = new com.amool.hexagonal.domain.model.User();
        creator.setId(10L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        Chapter chapter = new Chapter();
        chapter.setPublicationStatus("DRAFT");
        when(loadChapterPort.loadChapter(1L, 2L)).thenReturn(Optional.of(chapter));

        Instant when = Instant.now().plusSeconds(3600);
        chapterService.schedulePublication(1L, 2L, when, 10L);

        verify(updateChapterStatusPort).schedulePublication(1L, 2L, when);
    }

    @Test
    void cancelScheduledPublication_ShouldThrow_WhenUserNotAuthenticated() {
        assertThrows(SecurityException.class, () ->
                chapterService.cancelScheduledPublication(1L, 2L, null)
        );
        verifyNoInteractions(obtainWorkByIdPort, loadChapterPort, updateChapterStatusPort);
    }

    @Test
    void cancelScheduledPublication_ShouldThrow_WhenNotOwnerOrWorkMissing() {
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () ->
                chapterService.cancelScheduledPublication(1L, 2L, 10L)
        );

        var work = new Work();
        var creator = new com.amool.hexagonal.domain.model.User();
        creator.setId(999L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        assertThrows(SecurityException.class, () ->
                chapterService.cancelScheduledPublication(1L, 2L, 10L)
        );
    }

    @Test
    void cancelScheduledPublication_ShouldThrow_WhenChapterNotFound() {
        var work = new Work();
        var creator = new com.amool.hexagonal.domain.model.User();
        creator.setId(10L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        when(loadChapterPort.loadChapter(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                chapterService.cancelScheduledPublication(1L, 2L, 10L)
        );
        verify(updateChapterStatusPort, never()).clearSchedule(anyLong(), anyLong());
    }

    @Test
    void cancelScheduledPublication_ShouldCallPort_WhenOk() {
        var work = new Work();
        var creator = new com.amool.hexagonal.domain.model.User();
        creator.setId(10L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        Chapter chapter = new Chapter();
        chapter.setPublicationStatus("SCHEDULED");
        when(loadChapterPort.loadChapter(1L, 2L)).thenReturn(Optional.of(chapter));

        chapterService.cancelScheduledPublication(1L, 2L, 10L);
        verify(updateChapterStatusPort).clearSchedule(1L, 2L);
    }

    @Test
    void deleteChapter_ShouldThrowIllegalState_WhenNotDraft() {
        Long workId = 1L;
        Long chapterId = 2L;

        Chapter chapter = new Chapter();
        chapter.setPublicationStatus("PUBLISHED");
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.of(chapter));

        assertThrows(IllegalStateException.class, () ->
                chapterService.deleteChapter(workId, chapterId)
        );
        verify(deleteChapterPort, never()).deleteChapter(anyLong(), anyLong());
        verify(deleteChapterContentPort, never()).deleteContent(anyString(), anyString());
    }

    @Test
    void deleteChapter_ShouldDelete_WhenDraft() {
        Long workId = 1L;
        Long chapterId = 2L;

        Chapter chapter = new Chapter();
        chapter.setPublicationStatus("DRAFT");
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.of(chapter));

        chapterService.deleteChapter(workId, chapterId);

        verify(deleteChapterContentPort).deleteContent(workId.toString(), chapterId.toString());
        verify(deleteChapterPort).deleteChapter(workId, chapterId);
    }

    @Test
    void schedulePublication_ShouldThrowIllegalState_WhenNotDraft() {
        var work = new Work();
        var creator = new com.amool.hexagonal.domain.model.User();
        creator.setId(10L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));

        Chapter chapter = new Chapter();
        chapter.setPublicationStatus("PUBLISHED");
        when(loadChapterPort.loadChapter(1L, 2L)).thenReturn(Optional.of(chapter));

        assertThrows(IllegalStateException.class, () ->
                chapterService.schedulePublication(1L, 2L, Instant.now().plusSeconds(3600), 10L)
        );
        verify(updateChapterStatusPort, never()).schedulePublication(anyLong(), anyLong(), any());
    }

    @Test
    void cancelScheduledPublication_ShouldThrowIllegalState_WhenNotScheduled() {
        var work = new Work();
        var creator = new com.amool.hexagonal.domain.model.User();
        creator.setId(10L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));

        Chapter chapter = new Chapter();
        chapter.setPublicationStatus("DRAFT");
        when(loadChapterPort.loadChapter(1L, 2L)).thenReturn(Optional.of(chapter));

        assertThrows(IllegalStateException.class, () ->
                chapterService.cancelScheduledPublication(1L, 2L, 10L)
        );
        verify(updateChapterStatusPort, never()).clearSchedule(anyLong(), anyLong());
    }
}
