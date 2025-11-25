package com.amool.application.usecase;

import com.amool.application.port.out.RatingPort;
import com.amool.application.usecases.RateWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateWorkTest {

    @Mock
    private RatingPort ratingPort;

    private RateWork rateWork;
    @BeforeEach
    void setUp() {
        rateWork = new RateWork(ratingPort);
    }

    @Test
    void rateWork_WithValidRating_ShouldReturnRating() {
        Long workId = 1L;
        Long userId = 1L;
        double ratingValue = 4.5;
        double expectedRating = 4.5;

        when(ratingPort.rateWork(eq(workId), eq(userId), eq(ratingValue), any(LocalDateTime.class))).thenReturn(expectedRating);

        double result = rateWork.execute(workId, userId, ratingValue);

        assertEquals(expectedRating, result);
        verify(ratingPort).rateWork(eq(workId), eq(userId), eq(ratingValue), any(LocalDateTime.class));
    }

    @Test
    void rateWork_WithInvalidRating_ShouldThrowException() {
        Long workId = 1L;
        Long userId = 1L;
        double invalidRating = 6.0;
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> rateWork.execute(workId, userId, invalidRating)
        );
        
        assertEquals("El rating debe estar entre 0.5 y 5.0 en incrementos de 0.5", exception.getMessage());
        verifyNoInteractions(ratingPort);
    }

    @Test
    void rateWork_WithMinimumValidRating_ShouldSucceed() {
        Long workId = 1L;
        Long userId = 1L;
        double minRating = 0.5;
        double expectedRating = 0.5;


        when(ratingPort.rateWork(eq(workId), eq(userId), eq(minRating), any(LocalDateTime.class)))
                .thenReturn(expectedRating);

        double result = rateWork.execute(workId, userId, minRating);

        assertEquals(expectedRating, result);
        verify(ratingPort).rateWork(eq(workId), eq(userId), eq(minRating), any(LocalDateTime.class));
    }

    @Test
    void rateWork_WithMaximumValidRating_ShouldSucceed() {
        Long workId = 1L;
        Long userId = 1L;
        double maxRating = 5.0;
        double expectedRating = 5.0;

        when(ratingPort.rateWork(eq(workId), eq(userId), eq(maxRating), any(LocalDateTime.class))).thenReturn(expectedRating);

        double result = rateWork.execute(workId, userId, maxRating);

        assertEquals(expectedRating, result);
        verify(ratingPort).rateWork(eq(workId), eq(userId), eq(maxRating), any(LocalDateTime.class));
    }
}
