package com.amool.application.usecase;

import com.amool.application.port.out.RatingPort;
import com.amool.application.port.out.RatingPort.RatingDto;
import com.amool.application.usecases.GetWorkRatingsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetWorkRatingsUseCaseTest {

    @Mock
    private RatingPort ratingPort;

    private GetWorkRatingsUseCase getWorkRatingsUseCase;

    @BeforeEach
    void setUp() {
        getWorkRatingsUseCase = new GetWorkRatingsUseCase(ratingPort);
    }

    @Test
    void getWorkRatings_WithRatings_ShouldReturnWorkRatings() {
        Long workId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("rating").descending());
        Double averageRating = 4.5;
        List<RatingDto> ratings = List.of(
            new RatingDto(1L, 5.0),
            new RatingDto(2L, 4.0)
        );
        Page<RatingDto> ratingsPage = new PageImpl<>(ratings, pageable, 2);

        when(ratingPort.getAverageRating(workId)).thenReturn(averageRating);
        when(ratingPort.getWorkRatings(workId, pageable)).thenReturn(ratingsPage);

        var result = getWorkRatingsUseCase.execute(workId, pageable);

        assertAll(
            () -> assertEquals(workId, result.getWorkId()),
            () -> assertEquals(averageRating, result.getAverageRating()),
            () -> assertEquals(2, result.getTotalRatings()),
            () -> assertEquals(2, result.getRatings().size()),
            () -> assertEquals(5.0, result.getRatings().get(0).rating()),
            () -> assertEquals(4.0, result.getRatings().get(1).rating())
        );

        verify(ratingPort).getAverageRating(workId);
        verify(ratingPort).getWorkRatings(workId, pageable);
    }

    @Test
    void getWorkRatings_WithNoRatings_ShouldReturnEmptyResults() {
        Long workId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<RatingDto> emptyPage = Page.empty(pageable);

        when(ratingPort.getAverageRating(workId)).thenReturn(null);
        when(ratingPort.getWorkRatings(workId, pageable)).thenReturn(emptyPage);

        var result = getWorkRatingsUseCase.execute(workId, pageable);

        assertAll(
            () -> assertEquals(workId, result.getWorkId()),
            () -> assertNull(result.getAverageRating()),
            () -> assertEquals(0, result.getTotalRatings()),
            () -> assertTrue(result.getRatings().isEmpty())
        );

        verify(ratingPort).getAverageRating(workId);
        verify(ratingPort).getWorkRatings(workId, pageable);
    }

    @Test
    void getWorkRatings_WithPagination_ShouldReturnCorrectPage() {
        Long workId = 1L;
        Pageable firstPage = PageRequest.of(0, 1);
        Pageable secondPage = PageRequest.of(1, 1);
        
        List<RatingDto> firstPageRatings = List.of(new RatingDto(1L, 5.0));
        List<RatingDto> secondPageRatings = List.of(new RatingDto(2L, 4.0));
        
        Page<RatingDto> firstPageResult = new PageImpl<>(firstPageRatings, firstPage, 2);
        Page<RatingDto> secondPageResult = new PageImpl<>(secondPageRatings, secondPage, 2);

        when(ratingPort.getAverageRating(workId)).thenReturn(4.5);
        when(ratingPort.getWorkRatings(workId, firstPage)).thenReturn(firstPageResult);
        when(ratingPort.getWorkRatings(workId, secondPage)).thenReturn(secondPageResult);

        var firstResult = getWorkRatingsUseCase.execute(workId, firstPage);
        var secondResult = getWorkRatingsUseCase.execute(workId, secondPage);

        assertAll(
            () -> assertEquals(1, firstResult.getRatings().size()),
            () -> assertEquals(5.0, firstResult.getRatings().get(0).rating()),
            () -> assertEquals(1, secondResult.getRatings().size()),
            () -> assertEquals(4.0, secondResult.getRatings().get(0).rating())
        );

        verify(ratingPort, times(2)).getAverageRating(workId);
        verify(ratingPort).getWorkRatings(workId, firstPage);
        verify(ratingPort).getWorkRatings(workId, secondPage);
    }
}
