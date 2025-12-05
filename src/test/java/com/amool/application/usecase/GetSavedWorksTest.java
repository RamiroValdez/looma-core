package com.amool.application.usecase;

import com.amool.application.port.out.SaveWorkPort;
import com.amool.application.usecases.GetSavedWorks;
import com.amool.domain.model.Work;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSavedWorksTest {

    @Mock
    private SaveWorkPort saveWorkPort;

    @InjectMocks
    private GetSavedWorks getSavedWorks;

    private final Long TEST_USER_ID = 1L;

    private Work work(Long id, String title) {
        Work w = new Work();
        w.setId(id);
        w.setTitle(title);
        return w;
    }

    private void givenUserHasSavedWorks(Long userId, List<Work> works) {
        when(saveWorkPort.getSavedWorksByUser(userId)).thenReturn(works);
    }

    private void givenUserHasNoSavedWorks(Long userId) {
        when(saveWorkPort.getSavedWorksByUser(userId)).thenReturn(List.of());
    }

    private List<Work> whenGetSavedWorks(Long userId) {
        return getSavedWorks.execute(userId);
    }

    private void thenResultHasWorks(List<Work> result, int expectedSize, List<Work> expectedWorks) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        assertEquals(expectedWorks, result);
    }

    private void thenResultEmpty(List<Work> result) {
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void thenRepositoryCalledOnce(Long userId) {
        verify(saveWorkPort, times(1)).getSavedWorksByUser(userId);
    }

    @Test
    void execute_ShouldReturnListOfSavedWorks() {
        Work work1 = work(1L, "Work 1");
        Work work2 = work(2L, "Work 2");
        List<Work> expectedWorks = Arrays.asList(work1, work2);
        givenUserHasSavedWorks(TEST_USER_ID, expectedWorks);

        List<Work> result = whenGetSavedWorks(TEST_USER_ID);

        thenResultHasWorks(result, 2, expectedWorks);
        thenRepositoryCalledOnce(TEST_USER_ID);
    }

    @Test
    void execute_WhenNoSavedWorks_ShouldReturnEmptyList() {
        givenUserHasNoSavedWorks(TEST_USER_ID);

        List<Work> result = whenGetSavedWorks(TEST_USER_ID);

        thenResultEmpty(result);
        thenRepositoryCalledOnce(TEST_USER_ID);
    }
}
