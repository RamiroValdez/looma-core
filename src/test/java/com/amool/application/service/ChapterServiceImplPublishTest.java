package com.amool.application.service;
/*
import com.amool.application.port.out.*;
import com.amool.hexagonal.application.port.out.*;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ChapterServiceImplPublishTest {

    @Mock private LoadChapterPort loadChapterPort;
    @Mock private LoadChapterContentPort loadChapterContentPort;
    @Mock private SaveChapterPort saveChapterPort;
    @Mock private SaveChapterContentPort saveChapterContentPort;
    @Mock private DeleteChapterPort deleteChapterPort;
    @Mock private DeleteChapterContentPort deleteChapterContentPort;
    @Mock private ObtainWorkByIdPort obtainWorkByIdPort;
    @Mock private LoadLanguagePort loadLanguagePort;
    @Mock private UpdateChapterStatusPort updateChapterStatusPort;

    @InjectMocks
    private ChapterServiceImpl chapterService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void publishChapter_shouldThrowSecurity_whenUserNotAuthenticated() {
        assertThrows(SecurityException.class, () ->
                chapterService.publishChapter(1L, 1L, null)
        );
        verifyNoInteractions(obtainWorkByIdPort, loadChapterPort, updateChapterStatusPort);
    }

    @Test
    void publishChapter_shouldThrowNoSuchElement_whenWorkNotFound() {
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.empty());
        assertThrows(java.util.NoSuchElementException.class, () ->
                chapterService.publishChapter(1L, 1L, 10L)
        );
        verify(obtainWorkByIdPort).obtainWorkById(1L);
        verifyNoMoreInteractions(loadChapterPort, updateChapterStatusPort);
    }

    @Test
    void publishChapter_shouldThrowSecurity_whenUserIsNotCreator() {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(99L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));

        assertThrows(SecurityException.class, () ->
                chapterService.publishChapter(1L, 1L, 10L)
        );
        verify(obtainWorkByIdPort).obtainWorkById(1L);
        verifyNoMoreInteractions(loadChapterPort, updateChapterStatusPort);
    }

    @Test
    void publishChapter_shouldThrowNoSuchElement_whenChapterNotFound() {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(10L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        when(loadChapterPort.loadChapter(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () ->
                chapterService.publishChapter(1L, 1L, 10L)
        );
        verify(loadChapterPort).loadChapter(1L, 1L);
        verifyNoInteractions(updateChapterStatusPort);
    }

    @Test
    void publishChapter_shouldUpdateStatus_whenOk() {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(10L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        Chapter chapter = new Chapter();
        chapter.setPublicationStatus("DRAFT");
        when(loadChapterPort.loadChapter(1L, 1L)).thenReturn(Optional.of(chapter));

        chapterService.publishChapter(1L, 1L, 10L);

        verify(updateChapterStatusPort).updatePublicationStatus(eq(1L), eq(1L), eq("PUBLISHED"), any(LocalDateTime.class));
    }

    @Test
    void publishChapter_shouldThrowIllegalState_whenStatusIsNotDraft() {
        Work work = new Work();
        work.setId(1L);
        User creator = new User();
        creator.setId(10L);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));

        Chapter chapter = new Chapter();
        chapter.setPublicationStatus("PUBLISHED");
        when(loadChapterPort.loadChapter(1L, 1L)).thenReturn(Optional.of(chapter));

        assertThrows(IllegalStateException.class, () ->
                chapterService.publishChapter(1L, 1L, 10L)
        );
        verifyNoInteractions(updateChapterStatusPort);
    }
}
*/