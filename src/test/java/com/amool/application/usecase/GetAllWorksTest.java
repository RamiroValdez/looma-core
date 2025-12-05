package com.amool.application.usecase;

import com.amool.application.port.out.WorkPort;
import com.amool.application.usecases.GetAllWorks;
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
class GetAllWorksTest {

    @Mock
    private WorkPort workPort;

    @InjectMocks
    private GetAllWorks getAllWorks;

    private Work createWork(Long id, String title) {
        Work w = new Work();
        w.setId(id);
        w.setTitle(title);
        return w;
    }

    private void givenWorks(List<Work> works) {
        when(workPort.getAllWorks()).thenReturn(works);
    }

    private void givenNoWorks() {
        when(workPort.getAllWorks()).thenReturn(List.of());
    }

    private List<Work> whenExecute() {
        return getAllWorks.execute();
    }

    private void thenSizeIs(List<Work> result, int expected) {
        assertNotNull(result, "Result should not be null");
        assertEquals(expected, result.size(), "Should return " + expected + " works");
    }

    private void thenTitleIs(List<Work> result, int index, String expectedTitle) {
        assertEquals(expectedTitle, result.get(index).getTitle(), "Work title should match");
    }

    private void thenGetAllCalledOnce() {
        verify(workPort, times(1)).getAllWorks();
    }

    @Test
    void execute_shouldReturnListOfWorks() {
        List<Work> expectedWorks = Arrays.asList(
                createWork(1L, "Work 1"),
                createWork(2L, "Work 2")
        );
        givenWorks(expectedWorks);

        List<Work> result = whenExecute();

        thenSizeIs(result, 2);
        thenTitleIs(result, 0, "Work 1");
        thenTitleIs(result, 1, "Work 2");
        thenGetAllCalledOnce();
    }

    @Test
    void execute_whenNoWorksExist_shouldReturnEmptyList() {
        givenNoWorks();

        List<Work> result = whenExecute();

        thenSizeIs(result, 0);
        assertTrue(result.isEmpty(), "Result should be an empty list");
        thenGetAllCalledOnce();
    }
}
