package com.amool.application.usecase;

import com.amool.application.port.out.RatingPort;
import com.amool.application.usecases.GetUserRating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetUserRatingTest {

    @Mock
    private RatingPort ratingPort;

    private GetUserRating getUserRating;

    @BeforeEach
    void setUp() {
        getUserRating = new GetUserRating(ratingPort);
    }

    @Test
    void getUserRating_WhenRatingExists_ShouldReturnRating() {
        Long workId = 1L;
        Long userId = 1L;
        Double expectedRating = 4.5;
        
        when(ratingPort.getUserRating(workId, userId))
            .thenReturn(Optional.of(expectedRating));

        Optional<Double> result = getUserRating.execute(workId, userId);

        assertTrue(result.isPresent());
        assertEquals(expectedRating, result.get());
        verify(ratingPort).getUserRating(workId, userId);
    }

    @Test
    void getUserRating_WhenRatingDoesNotExist_ShouldReturnEmpty() {
        Long workId = 1L;
        Long userId = 1L;
        
        when(ratingPort.getUserRating(workId, userId))
            .thenReturn(Optional.empty());

        Optional<Double> result = getUserRating.execute(workId, userId);

        assertTrue(result.isEmpty());
        verify(ratingPort).getUserRating(workId, userId);
    }

    @Test
    void getUserRating_WithDifferentUsers_ShouldReturnCorrectRatings() {
        Long workId = 1L;
        Long userId1 = 1L;
        Long userId2 = 2L;
        Double rating1 = 4.5;
        Double rating2 = 3.5;
        
        when(ratingPort.getUserRating(workId, userId1)).thenReturn(Optional.of(rating1));
        when(ratingPort.getUserRating(workId, userId2)).thenReturn(Optional.of(rating2));

        Optional<Double> result1 = getUserRating.execute(workId, userId1);
        Optional<Double> result2 = getUserRating.execute(workId, userId2);

        assertAll(
            () -> assertTrue(result1.isPresent()),
            () -> assertEquals(rating1, result1.get()),
            () -> assertTrue(result2.isPresent()),
            () -> assertEquals(rating2, result2.get())
        );
        
        verify(ratingPort).getUserRating(workId, userId1);
        verify(ratingPort).getUserRating(workId, userId2);
    }
}
