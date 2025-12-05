package com.amool.application.usecase;


import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.UpdateChapterStatusPort;
import com.amool.application.usecases.CancelScheduledPublication;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CancelScheduledPublicationTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private LoadChapterPort loadChapterPort;
    private UpdateChapterStatusPort updateChapterStatusPort;
    private CancelScheduledPublication useCase;

    @BeforeEach
    public void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        updateChapterStatusPort = Mockito.mock(UpdateChapterStatusPort.class);
        useCase = new CancelScheduledPublication(
                obtainWorkByIdPort,
                loadChapterPort,
                updateChapterStatusPort
        );
    }

    private void givenWorkNotFound(Long workId) {
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.empty());
    }

    private void givenWorkWithNullCreator(Long workId) {
        Work work = new Work();
        work.setCreator(null);
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));
    }

    private void givenWorkWithCreator(Long workId, Long creatorId) {
        Work work = new Work();
        User creator = new User();
        creator.setId(creatorId);
        work.setCreator(creator);
        when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));
    }

    private void givenChapterNotFound(Long workId, Long chapterId) {
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.empty());
    }

    private void givenChapterWithStatus(Long workId, Long chapterId, String status) {
        Chapter chapter = new Chapter();
        chapter.setPublicationStatus(status);
        when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.of(chapter));
    }

    private void whenCancel(Long workId, Long chapterId, Long authenticatedUserId) {
        useCase.execute(workId, chapterId, authenticatedUserId);
    }

    private void thenThrowsSecurity(Runnable action) {
        assertThrows(SecurityException.class, action::run);
    }

    private void thenThrowsNoSuchElement(Runnable action) {
        assertThrows(NoSuchElementException.class, action::run);
    }

    private void thenThrowsIllegalState(Runnable action) {
        assertThrows(IllegalStateException.class, action::run);
    }

    private void thenDoesNotThrow(Runnable action) {
        assertDoesNotThrow(action::run);
    }

    private void thenScheduleCleared(Long workId, Long chapterId) {
        verify(updateChapterStatusPort, times(1)).clearSchedule(workId, chapterId);
    }

    private Runnable cancelAction(Long workId, Long chapterId, Long authenticatedUserId) {
        return () -> whenCancel(workId, chapterId, authenticatedUserId);
    }

    @Test
    public void when_authenticatedUserIsNull_then_throwSecurityException() {
        thenThrowsSecurity(cancelAction(1L, 1L, null));
    }

    @Test
    public void when_workIdNotExistsInDatabase_then_throwNoSuchElementException() {
        givenWorkNotFound(1L);
        thenThrowsNoSuchElement(cancelAction(1L, 1L, 1L));
    }

    @Test
    public void when_workCreatorIsNull_then_throwSecurityException() {
        givenWorkWithNullCreator(1L);
        thenThrowsSecurity(cancelAction(1L, 1L, 1L));
    }

    @Test
    public void when_workCreatorIsDifferentFromAuthenticated_then_throwSecurityException() {
        givenWorkWithCreator(1L, 2L);
        thenThrowsSecurity(cancelAction(1L, 1L, 1L));
    }

    @Test
    public void when_chapterIdNotExistsForWork_then_throwNoSuchElementException() {
        givenWorkWithCreator(1L, 1L);
        givenChapterNotFound(1L, 1L);

        thenThrowsNoSuchElement(cancelAction(1L, 1L, 1L));
    }

    @Test
    public void when_chapterIsNotInSCHEDULEDState_then_throwIllegalStateException() {
        givenWorkWithCreator(1L, 1L);
        givenChapterWithStatus(1L, 1L, "?");

        thenThrowsIllegalState(cancelAction(1L, 1L, 1L));
    }

    @Test
    public void when_allParametersAreValid_then_executeSuccessfully() {
        givenWorkWithCreator(1L, 1L);
        givenChapterWithStatus(1L, 1L, "SCHEDULED");

        thenDoesNotThrow(cancelAction(1L, 1L, 1L));
        thenScheduleCleared(1L, 1L);
    }
}
