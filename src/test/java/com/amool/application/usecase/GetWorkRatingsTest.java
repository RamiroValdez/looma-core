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

    @Test
    void getWorkRatings_WithRatings_ShouldReturnTotalCount() {
        Long workId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("rating").descending());
        Integer expectedTotalRatings = 2;

        when(ratingPort.getTotalRatingsCount(workId)).thenReturn(expectedTotalRatings);

        Integer result = getWorkRatings.execute(workId, pageable);

        assertEquals(expectedTotalRatings, result);
        verify(ratingPort).getTotalRatingsCount(workId);
    }

    @Test
    void getWorkRatings_WithNoRatings_ShouldReturnZero() {
        Long workId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(ratingPort.getTotalRatingsCount(workId)).thenReturn(0);

        Integer result = getWorkRatings.execute(workId, pageable);

        assertEquals(0, result);
        verify(ratingPort).getTotalRatingsCount(workId);
    }

    @Test
    void getWorkRatings_WithDifferentWorkIds_ShouldReturnCorrectTotal() {
        Long workId1 = 1L;
        Long workId2 = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        when(ratingPort.getTotalRatingsCount(workId1)).thenReturn(5);
        when(ratingPort.getTotalRatingsCount(workId2)).thenReturn(3);

        Integer result1 = getWorkRatings.execute(workId1, pageable);
        Integer result2 = getWorkRatings.execute(workId2, pageable);

        assertAll(
            () -> assertEquals(5, result1),
            () -> assertEquals(3, result2)
        );

        verify(ratingPort).getTotalRatingsCount(workId1);
        verify(ratingPort).getTotalRatingsCount(workId2);
    }
}
