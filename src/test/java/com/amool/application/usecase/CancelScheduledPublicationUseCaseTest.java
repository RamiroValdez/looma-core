package com.amool.application.usecase;


import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.UpdateChapterStatusPort;
import com.amool.application.usecases.CancelScheduledPublicationUseCase;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.User;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CancelScheduledPublicationUseCaseTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private LoadChapterPort loadChapterPort;
    private UpdateChapterStatusPort updateChapterStatusPort;
    private CancelScheduledPublicationUseCase useCase;

    @BeforeEach
    public void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        loadChapterPort = Mockito.mock(LoadChapterPort.class);
        updateChapterStatusPort = Mockito.mock(UpdateChapterStatusPort.class);
        useCase = new CancelScheduledPublicationUseCase(
                obtainWorkByIdPort,
                loadChapterPort,
                updateChapterStatusPort
        );
    }

    @Test
    public void when_authenticatedUserIsNull_then_throwSecurityException() {

        assertThrows(SecurityException.class, () -> useCase.execute(1L,1L, null));

    }

    @Test
    public void when_workIdNotExistsInDatabase_then_throwNoSuchElementException(){

        assertThrows(NoSuchElementException.class, () -> useCase.execute(1L,1L, 1L));

    }

    @Test
    public void when_workCreatorIsNull_then_throwSecurityException(){

        Work work = new Work();
        work.setCreator(null);

        Mockito.when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));


        assertThrows(SecurityException.class, () -> useCase.execute(1L,1L, 1L));

    }

    @Test
    public void when_workCreatorIsDifferentFromAuthenticated_then_throwSecurityException(){

        User authenticationUser = new User();
        Work work = new Work();
        Long creatorWorkId = 2L;

        authenticationUser.setId(1L);
        work.setCreator(authenticationUser);

        Mockito.when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));


        assertThrows(SecurityException.class, () -> useCase.execute(1L,1L, creatorWorkId));

    }

    @Test
    public void when_chapterIdNotExistsForWork_then_throwNoSuchElementException(){

        User authenticationUser = new User();
        Work work = new Work();

        authenticationUser.setId(1L);
        work.setCreator(authenticationUser);

        Mockito.when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        Mockito.when(loadChapterPort.loadChapter(1L,1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> useCase.execute(1L,1L, 1L));
    }

    @Test
    public void when_chapterIsNotInSCHEDULEDState_then_throwIllegalStateException(){
        User authenticationUser = new User();
        Work work = new Work();
        Chapter chapter = new Chapter();

        authenticationUser.setId(1L);
        work.setCreator(authenticationUser);
        chapter.setPublicationStatus("?");

        Mockito.when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        Mockito.when(loadChapterPort.loadChapter(1L,1L)).thenReturn(Optional.of(chapter));

        assertThrows(IllegalStateException.class, () -> useCase.execute(1L,1L, 1L));
    }

    @Test
    public void when_allParametersAreValid_then_executeSuccessfully(){
        User authenticationUser = new User();
        Work work = new Work();
        Chapter chapter = new Chapter();

        authenticationUser.setId(1L);
        work.setCreator(authenticationUser);
        chapter.setPublicationStatus("SCHEDULED");

        Mockito.when(obtainWorkByIdPort.obtainWorkById(1L)).thenReturn(Optional.of(work));
        Mockito.when(loadChapterPort.loadChapter(1L,1L)).thenReturn(Optional.of(chapter));

        assertDoesNotThrow(() -> useCase.execute(1L,1L, 1L));

        Mockito.verify(updateChapterStatusPort, Mockito.times(1)).clearSchedule(1L,1L);
    }
}
