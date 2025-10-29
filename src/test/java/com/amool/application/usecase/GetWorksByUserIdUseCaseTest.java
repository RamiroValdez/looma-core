package com.amool.application.usecase;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.User;
import com.amool.domain.model.Language;
import com.amool.application.usecases.GetWorksByUserIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GetWorksByUserIdUseCaseTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private GetWorksByUserIdUseCase useCase;
    
    private static final Long USER_ID = 1L;
    private static final Long WORK_ID_1 = 10L;
    private static final Long WORK_ID_2 = 20L;
    private static final String WORK_TITLE_1 = "Test Work 1";
    private static final String WORK_TITLE_2 = "Test Work 2";

    @BeforeEach
    public void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        useCase = new GetWorksByUserIdUseCase(obtainWorkByIdPort);
    }

    @Test
    public void when_UserHasWorks_ThenReturnWorksList() {
        Work work1 = createWork(WORK_ID_1, WORK_TITLE_1, USER_ID);
        Work work2 = createWork(WORK_ID_2, WORK_TITLE_2, USER_ID);
        List<Work> expectedWorks = Arrays.asList(work1, work2);
        
        when(obtainWorkByIdPort.getWorksByUserId(USER_ID)).thenReturn(expectedWorks);

        List<Work> result = useCase.execute(USER_ID);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(WORK_TITLE_1, result.get(0).getTitle());
        assertEquals(WORK_TITLE_2, result.get(1).getTitle());
        assertEquals(USER_ID, result.get(0).getCreator().getId());
        assertEquals(USER_ID, result.get(1).getCreator().getId());
    }

    @Test
    public void when_UserHasNoWorks_ThenReturnEmptyList() {
        when(obtainWorkByIdPort.getWorksByUserId(USER_ID)).thenReturn(Collections.emptyList());

        List<Work> result = useCase.execute(USER_ID);

        assertTrue(result.isEmpty());
    }

    private Work createWork(Long id, String title, Long creatorId) {
        Work work = new Work();
        work.setId(id);
        work.setTitle(title);
        work.setDescription("Description for " + title);
        work.setState("PUBLISHED");
        
        User creator = new User();
        creator.setId(creatorId);
        creator.setUsername("user" + creatorId);
        work.setCreator(creator);
        
        Language language = new Language();
        language.setId(1L);
        language.setName("Spanish");
        language.setCode("es");
        work.setOriginalLanguage(language);
        
        return work;
    }
}
