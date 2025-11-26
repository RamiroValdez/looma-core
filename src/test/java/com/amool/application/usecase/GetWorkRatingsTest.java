package com.amool.application.usecase;

import com.amool.application.port.out.RatingPort;
import com.amool.application.usecases.GetWorkRatings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetWorkRatingsTest {

    @Mock
    private RatingPort ratingPort;

    private GetWorkRatings getWorkRatings;

    @BeforeEach
    void setUp() {
        getWorkRatings = new GetWorkRatings(ratingPort);
    }

    private void givenTotalRatingsCount(Long workId, int total) {
        when(ratingPort.getTotalRatingsCount(workId)).thenReturn(total);
    }

    private Integer whenGetWorkRatings(Long workId, Pageable pageable) {
        return getWorkRatings.execute(workId, pageable);
    }

    private void thenTotalIs(Integer result, int expected) {
        assertEquals(expected, result);
    }

    private void thenPortCalledWith(Long workId) {
        verify(ratingPort).getTotalRatingsCount(workId);
    }

    @Test
    void getWorkRatings_WithRatings_ShouldReturnTotalCount() {
        Long workId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("rating").descending());
        givenTotalRatingsCount(workId, 2);

        Integer result = whenGetWorkRatings(workId, pageable);

        thenTotalIs(result, 2);
        thenPortCalledWith(workId);
    }

    @Test
    void getWorkRatings_WithNoRatings_ShouldReturnZero() {
        Long workId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        givenTotalRatingsCount(workId, 0);

        Integer result = whenGetWorkRatings(workId, pageable);

        thenTotalIs(result, 0);
        thenPortCalledWith(workId);
    }

    @Test
    void getWorkRatings_WithDifferentWorkIds_ShouldReturnCorrectTotal() {
        Long workId1 = 1L;
        Long workId2 = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        givenTotalRatingsCount(workId1, 5);
        givenTotalRatingsCount(workId2, 3);

        Integer result1 = whenGetWorkRatings(workId1, pageable);
        Integer result2 = whenGetWorkRatings(workId2, pageable);

        assertAll(
            () -> thenTotalIs(result1, 5),
            () -> thenTotalIs(result2, 3)
        );
        thenPortCalledWith(workId1);
        thenPortCalledWith(workId2);
    }
}
