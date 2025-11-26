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

    private void givenRatingExists(Long workId, Long userId, Double rating) {
        when(ratingPort.getUserRating(workId, userId)).thenReturn(Optional.of(rating));
    }

    private void givenRatingNotExists(Long workId, Long userId) {
        when(ratingPort.getUserRating(workId, userId)).thenReturn(Optional.empty());
    }

    private Optional<Double> whenGetUserRating(Long workId, Long userId) {
        return getUserRating.execute(workId, userId);
    }

    private void thenRatingPresent(Optional<Double> result, Double expected) {
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    private void thenRatingEmpty(Optional<Double> result) {
        assertTrue(result.isEmpty());
    }

    private void thenPortCalled(Long workId, Long userId) {
        verify(ratingPort).getUserRating(workId, userId);
    }

    @Test
    void getUserRating_WhenRatingExists_ShouldReturnRating() {
        Long workId = 1L; Long userId = 1L; Double expectedRating = 4.5;
        givenRatingExists(workId, userId, expectedRating);

        Optional<Double> result = whenGetUserRating(workId, userId);

        thenRatingPresent(result, expectedRating);
        thenPortCalled(workId, userId);
    }

    @Test
    void getUserRating_WhenRatingDoesNotExist_ShouldReturnEmpty() {
        Long workId = 1L; Long userId = 1L;
        givenRatingNotExists(workId, userId);

        Optional<Double> result = whenGetUserRating(workId, userId);

        thenRatingEmpty(result);
        thenPortCalled(workId, userId);
    }

    @Test
    void getUserRating_WithDifferentUsers_ShouldReturnCorrectRatings() {
        Long workId = 1L; Long userId1 = 1L; Long userId2 = 2L; Double rating1 = 4.5; Double rating2 = 3.5;
        givenRatingExists(workId, userId1, rating1);
        givenRatingExists(workId, userId2, rating2);

        Optional<Double> result1 = whenGetUserRating(workId, userId1);
        Optional<Double> result2 = whenGetUserRating(workId, userId2);

        thenRatingPresent(result1, rating1);
        thenRatingPresent(result2, rating2);
        thenPortCalled(workId, userId1);
        thenPortCalled(workId, userId2);
    }
}
