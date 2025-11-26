package com.amool.application.usecase;

import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.UpdateChapterStatusPort;
import com.amool.application.usecases.PublishChapter;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PublishChapterTest {

    private LoadChapterPort loadChapterPort;
    private ObtainWorkByIdPort obtainWorkByIdPort;
    private UpdateChapterStatusPort updateChapterStatusPort;
    private PublishChapter useCase;

    private final Long workId = 1L;
    private final Long chapterId = 10L;
    private final Long authenticatedUserId = 100L;
    private Work work;
    private Chapter chapter;

    @BeforeEach
    public void setUp() {
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        updateChapterStatusPort = Mockito.mock(UpdateChapterStatusPort.class);
        
        useCase = new PublishChapter(
            loadChapterPort,
            obtainWorkByIdPort,
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
    }

    @Test
    public void shouldPublishChapterWhenRequestIsValid() {
        givenWorkOwnedBy(authenticatedUserId);
        givenChapterWithStatus("DRAFT");

        whenPublishingChapter(authenticatedUserId);

        thenChapterIsPublished();
    }

    @Test
    public void shouldThrowSecurityExceptionWhenUserNotAuthenticated() {
        thenThrows(SecurityException.class, publishingChapter(null));
        thenNoPortsAreInvoked();
    }

    @Test
    public void shouldThrowNoSuchElementWhenWorkNotFound() {
        givenWorkDoesNotExist();

        thenThrows(NoSuchElementException.class, publishingChapter(authenticatedUserId));
        thenWorkLookupOccurs();
        thenChapterLookupDoesNotOccur();
        thenNoPublicationStatusUpdateOccurs();
    }

    @Test
    public void shouldThrowSecurityExceptionWhenUserNotAuthorized() {
        givenWorkOwnedBy(999L);

        thenThrows(SecurityException.class, publishingChapter(authenticatedUserId));
        thenWorkLookupOccurs();
        thenChapterLookupDoesNotOccur();
        thenNoPublicationStatusUpdateOccurs();
    }

    @Test
    public void shouldThrowIllegalStateWhenChapterNotInDraft() {
        givenWorkOwnedBy(authenticatedUserId);
        givenChapterWithStatus("PUBLISHED");

        thenThrows(IllegalStateException.class, publishingChapter(authenticatedUserId));
        thenWorkLookupOccurs();
        thenChapterLookupOccurs();
        thenNoPublicationStatusUpdateOccurs();
    }

    @Test
    public void shouldThrowNoSuchElementWhenChapterNotFound() {
        givenWorkOwnedBy(authenticatedUserId);
        givenChapterDoesNotExist();

        thenThrows(NoSuchElementException.class, publishingChapter(authenticatedUserId));
        thenWorkLookupOccurs();
        thenChapterLookupOccurs();
        thenNoPublicationStatusUpdateOccurs();
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

    private void whenPublishingChapter(Long userId) {
        useCase.execute(workId, chapterId, userId);
    }

    private Executable publishingChapter(Long userId) {
        return () -> useCase.execute(workId, chapterId, userId);
    }

    private void thenChapterIsPublished() {
        verify(updateChapterStatusPort).updatePublicationStatus(
            eq(workId),
            eq(chapterId),
            eq("PUBLISHED"),
            any(LocalDateTime.class)
        );
    }

    private void thenThrows(Class<? extends Throwable> expectedException, Executable action) {
        assertThrows(expectedException, action);
    }

    private void thenNoPortsAreInvoked() {
        verify(obtainWorkByIdPort, never()).obtainWorkById(any());
        verify(loadChapterPort, never()).loadChapter(any(), any());
        thenNoPublicationStatusUpdateOccurs();
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

    private void thenNoPublicationStatusUpdateOccurs() {
        verify(updateChapterStatusPort, never()).updatePublicationStatus(any(), any(), any(), any());
    }
}
