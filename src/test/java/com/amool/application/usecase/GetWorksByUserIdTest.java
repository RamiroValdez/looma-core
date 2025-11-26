package com.amool.application.usecase;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.User;
import com.amool.domain.model.Language;
import com.amool.application.usecases.GetWorksByUserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GetWorksByUserIdTest {

    private ObtainWorkByIdPort obtainWorkByIdPort;
    private GetWorksByUserId useCase;

    private static final Long USER_ID = 1L;
    private static final Long WORK_ID_1 = 10L;
    private static final Long WORK_ID_2 = 20L;
    private static final String WORK_TITLE_1 = "Test Work 1";
    private static final String WORK_TITLE_2 = "Test Work 2";

    @BeforeEach
    public void setUp() {
        obtainWorkByIdPort = Mockito.mock(ObtainWorkByIdPort.class);
        useCase = new GetWorksByUserId(obtainWorkByIdPort);
    }

    private void givenUserHasWorks(Long userId, List<Work> works) {
        when(obtainWorkByIdPort.getWorksByUserId(userId)).thenReturn(works);
    }

    private void givenUserHasNoWorks(Long userId) {
        when(obtainWorkByIdPort.getWorksByUserId(userId)).thenReturn(Collections.emptyList());
    }

    private List<Work> whenGetWorks(Long userId) {
        return useCase.execute(userId);
    }

    private void thenWorksReturned(List<Work> result, int expectedSize, List<String> expectedTitles, Long expectedCreatorId) {
        assertFalse(result.isEmpty());
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedTitles.size(); i++) {
            assertEquals(expectedTitles.get(i), result.get(i).getTitle());
            assertEquals(expectedCreatorId, result.get(i).getCreator().getId());
        }
    }

    private void thenWorksEmpty(List<Work> result) {
        assertTrue(result.isEmpty());
    }

    @Test
    public void when_UserHasWorks_ThenReturnWorksList() {
        Work work1 = createWork(WORK_ID_1, WORK_TITLE_1, USER_ID);
        Work work2 = createWork(WORK_ID_2, WORK_TITLE_2, USER_ID);
        givenUserHasWorks(USER_ID, Arrays.asList(work1, work2));

        List<Work> result = whenGetWorks(USER_ID);

        thenWorksReturned(result, 2, List.of(WORK_TITLE_1, WORK_TITLE_2), USER_ID);
    }

    @Test
    public void when_UserHasNoWorks_ThenReturnEmptyList() {
        givenUserHasNoWorks(USER_ID);

        List<Work> result = whenGetWorks(USER_ID);

        thenWorksEmpty(result);
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
