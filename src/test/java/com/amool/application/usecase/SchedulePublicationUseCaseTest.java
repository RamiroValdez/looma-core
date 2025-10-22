package com.amool.application.usecase;

import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.UpdateChapterStatusPort;
import com.amool.application.usecases.SchedulePublicationUseCase;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SchedulePublicationUseCaseTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private LoadChapterPort loadChapterPort;
    private UpdateChapterStatusPort updateChapterStatusPort;
    private SchedulePublicationUseCase useCase;

    private final Long workId = 1L;
    private final Long chapterId = 10L;
    private final Long authenticatedUserId = 100L;
    private Work work;
    private Chapter chapter;
    private Instant futureDate;

    @BeforeEach
    public void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        updateChapterStatusPort = Mockito.mock(UpdateChapterStatusPort.class);
        
        useCase = new SchedulePublicationUseCase(
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
        
        futureDate = Instant.now().plus(1, ChronoUnit.DAYS);
    }

    @Test
    public void when_ValidRequest_ThenSchedulePublication() {
        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
                .thenReturn(Optional.of(work));
                
        Mockito.when(loadChapterPort.loadChapter(workId, chapterId))
                .thenReturn(Optional.of(chapter));

        useCase.execute(workId, chapterId, futureDate, authenticatedUserId);

        verify(updateChapterStatusPort).schedulePublication(workId, chapterId, futureDate);
    }

    @Test
    public void when_UserNotAuthenticated_ThenThrowSecurityException() {
        assertThrows(SecurityException.class, () -> 
            useCase.execute(workId, chapterId, futureDate, null)
        );
        
        verifyNoInteractions();
    }

    @Test
    public void when_InvalidDate_ThenThrowIllegalArgumentException() {
        Instant pastDate = Instant.now().minus(1, ChronoUnit.HOURS);
        
        assertThrows(IllegalArgumentException.class, () -> 
            useCase.execute(workId, chapterId, pastDate, authenticatedUserId)
        );
        
        verifyNoInteractions();
    }

    @Test
    public void when_WorkNotFound_ThenThrowNoSuchElementException() {
        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> 
            useCase.execute(workId, chapterId, futureDate, authenticatedUserId)
        );
        
        verify(obtainWorkByIdPort).obtainWorkById(workId);
        verifyNoOtherInteractions();
    }

    @Test
    public void when_UserNotAuthorized_ThenThrowSecurityException() {
        User otherUser = new User();
        otherUser.setId(999L);
        work.setCreator(otherUser);
        
        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
                .thenReturn(Optional.of(work));

        assertThrows(SecurityException.class, () -> 
            useCase.execute(workId, chapterId, futureDate, authenticatedUserId)
        );
        
        verify(obtainWorkByIdPort).obtainWorkById(workId);
        verifyNoOtherInteractions();
    }

    @Test
    public void when_ChapterNotInDraft_ThenThrowIllegalStateException() {
        chapter.setPublicationStatus("PUBLISHED");
        
        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
                .thenReturn(Optional.of(work));
                
        Mockito.when(loadChapterPort.loadChapter(workId, chapterId))
                .thenReturn(Optional.of(chapter));

        assertThrows(IllegalStateException.class, () -> 
            useCase.execute(workId, chapterId, futureDate, authenticatedUserId)
        );
        
        verify(obtainWorkByIdPort).obtainWorkById(workId);
        verify(loadChapterPort).loadChapter(workId, chapterId);
        verifyNoOtherInteractions();
    }

    @Test
    public void when_ChapterNotFound_ThenThrowNoSuchElementException() {
        Mockito.when(obtainWorkByIdPort.obtainWorkById(workId))
                .thenReturn(Optional.of(work));
                
        Mockito.when(loadChapterPort.loadChapter(workId, chapterId))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> 
            useCase.execute(workId, chapterId, futureDate, authenticatedUserId)
        );
        
        verify(obtainWorkByIdPort).obtainWorkById(workId);
        verify(loadChapterPort).loadChapter(workId, chapterId);
        verifyNoOtherInteractions();
    }

    @Test
    public void when_NullDate_ThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> 
            useCase.execute(workId, chapterId, null, authenticatedUserId)
        );
        
        verifyNoInteractions();
    }

    private void verifyNoInteractions() {
        verify(obtainWorkByIdPort, never()).obtainWorkById(any());
        verify(loadChapterPort, never()).loadChapter(any(), any());
        verify(updateChapterStatusPort, never()).schedulePublication(any(), any(), any());
    }
    
    private void verifyNoOtherInteractions() {
        verify(updateChapterStatusPort, never()).schedulePublication(any(), any(), any());
    }
}
