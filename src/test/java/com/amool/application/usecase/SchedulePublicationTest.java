package com.amool.application.usecase;

import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.UpdateChapterStatusPort;
import com.amool.application.usecases.SchedulePublication;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.function.Executable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SchedulePublicationTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private LoadChapterPort loadChapterPort;
    private UpdateChapterStatusPort updateChapterStatusPort;
    private SchedulePublication useCase;

    private final Long workId = 1L;
    private final Long chapterId = 10L;
    private final Long authenticatedUserId = 100L;
    private Work work;
    private Chapter chapter;
    private Instant scheduleDate;

    @BeforeEach
    public void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        updateChapterStatusPort = Mockito.mock(UpdateChapterStatusPort.class);
        
        useCase = new SchedulePublication(
            obtainWorkByIdPort,
            loadChapterPort,
            updateChapterStatusPort
        );

        User creator = new User();
        creator.setId(authenticatedUserId);
        
        work = new Work();
        work.setId(workId);
        work.setCreator(creator);
        
        chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setWorkId(workId);
        chapter.setPublicationStatus("DRAFT");
        
        scheduleDate = Instant.now().plus(1, ChronoUnit.DAYS);
    }

    @Test
    public void shouldSchedulePublicationWhenRequestIsValid() {
        givenWorkOwnedBy(authenticatedUserId);
        givenChapterWithStatus("DRAFT");

        whenSchedulingPublication(authenticatedUserId, scheduleDate);

        thenPublicationIsScheduled(scheduleDate);
    }

    @Test
    public void shouldThrowSecurityExceptionWhenUserNotAuthenticated() {
        thenThrows(SecurityException.class, schedulingPublication(null, scheduleDate));
        thenNoPortsInvoked();
    }

    @Test
    public void shouldThrowIllegalArgumentWhenDateIsInThePast() {
        Instant invalidDate = pastInstant();

        thenThrows(IllegalArgumentException.class, schedulingPublication(authenticatedUserId, invalidDate));
        thenNoPortsInvoked();
    }

    @Test
    public void shouldThrowNoSuchElementWhenWorkNotFound() {
        givenWorkDoesNotExist();

        thenThrows(NoSuchElementException.class, schedulingPublication(authenticatedUserId, scheduleDate));
        thenWorkLookupOccurs();
        thenChapterLookupDoesNotOccur();
        thenNoScheduleTriggered();
    }

    @Test
    public void shouldThrowSecurityExceptionWhenUserNotAuthorized() {
        givenWorkOwnedBy(999L);

        thenThrows(SecurityException.class, schedulingPublication(authenticatedUserId, scheduleDate));
        thenWorkLookupOccurs();
        thenChapterLookupDoesNotOccur();
        thenNoScheduleTriggered();
    }

    @Test
    public void shouldThrowIllegalStateWhenChapterNotInDraft() {
        givenWorkOwnedBy(authenticatedUserId);
        givenChapterWithStatus("PUBLISHED");

        thenThrows(IllegalStateException.class, schedulingPublication(authenticatedUserId, scheduleDate));
        thenWorkLookupOccurs();
        thenChapterLookupOccurs();
        thenNoScheduleTriggered();
    }

    @Test
    public void shouldThrowNoSuchElementWhenChapterNotFound() {
        givenWorkOwnedBy(authenticatedUserId);
        givenChapterDoesNotExist();

        thenThrows(NoSuchElementException.class, schedulingPublication(authenticatedUserId, scheduleDate));
        thenWorkLookupOccurs();
        thenChapterLookupOccurs();
        thenNoScheduleTriggered();
    }

    @Test
    public void shouldThrowIllegalArgumentWhenDateIsNull() {
        thenThrows(IllegalArgumentException.class, schedulingPublication(authenticatedUserId, null));
        thenNoPortsInvoked();
    }

    private void givenWorkOwnedBy(Long ownerId) {
        User creator = new User();
        creator.setId(ownerId);
        work.setCreator(creator);
        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.of(work));
    }

    private void givenWorkDoesNotExist() {
        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId)).thenReturn(Optional.empty());
    }

    private void givenChapterWithStatus(String status) {
        chapter.setPublicationStatus(status);
        Mockito.when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.of(chapter));
    }

    private void givenChapterDoesNotExist() {
        Mockito.when(loadChapterPort.loadChapter(workId, chapterId)).thenReturn(Optional.empty());
    }

    private void whenSchedulingPublication(Long userId, Instant publicationDate) {
        useCase.execute(workId, chapterId, publicationDate, userId);
    }

    private Executable schedulingPublication(Long userId, Instant publicationDate) {
        return () -> useCase.execute(workId, chapterId, publicationDate, userId);
    }

    private void thenPublicationIsScheduled(Instant expectedDate) {
        verify(updateChapterStatusPort).schedulePublication(workId, chapterId, expectedDate);
    }

    private void thenThrows(Class<? extends Throwable> expectedException, Executable action) {
        assertThrows(expectedException, action);
    }

    private void thenNoPortsInvoked() {
        verify(obtainWorkByIdPort, never()).obtainWorkById(any());
        verify(loadChapterPort, never()).loadChapter(any(), any());
        thenNoScheduleTriggered();
    }

    private void thenWorkLookupOccurs() {
        verify(obtainWorkByIdPort).obtainWorkById(workId);
    }

    private void thenChapterLookupOccurs() {
        verify(loadChapterPort).loadChapter(workId, chapterId);
    }

    private void thenChapterLookupDoesNotOccur() {
        verify(loadChapterPort, never()).loadChapter(any(), any());
    }

    private void thenNoScheduleTriggered() {
        verify(updateChapterStatusPort, never()).schedulePublication(any(), any(), any());
    }

    private Instant pastInstant() {
        return Instant.now().minus(1, ChronoUnit.HOURS);
    }
}
