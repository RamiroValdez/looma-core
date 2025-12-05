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

    private static final Long WORK_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final double VALID_RATING = 4.5;
    private static final double MIN_RATING = 0.5;
    private static final double MAX_RATING = 5.0;
    private static final double INVALID_RATING = 6.0;

    @Mock
    private RatingPort ratingPort;

    private RateWork rateWork;
    @BeforeEach
    void setUp() {
        rateWork = new RateWork(ratingPort);
    }

    @Test
    void shouldReturnRatingWhenInputIsValid() {
        givenRatingPortReturns(VALID_RATING);

        double result = whenRatingWork(VALID_RATING);

        thenRatingReturned(result, VALID_RATING);
        thenPortInvokedWith(VALID_RATING);
    }

    @Test
    void shouldFailWhenRatingIsOutOfRange() {
        thenRatingFailsDueToInvalidValue(INVALID_RATING);
        thenPortNotInvoked();
    }

    @Test
    void shouldSucceedWithMinimumValidRating() {
        givenRatingPortReturns(MIN_RATING);

        double result = whenRatingWork(MIN_RATING);

        thenRatingReturned(result, MIN_RATING);
        thenPortInvokedWith(MIN_RATING);
    }

    @Test
    void shouldSucceedWithMaximumValidRating() {
        givenRatingPortReturns(MAX_RATING);

        double result = whenRatingWork(MAX_RATING);

        thenRatingReturned(result, MAX_RATING);
        thenPortInvokedWith(MAX_RATING);
    }

    private void givenRatingPortReturns(double rating) {
        when(ratingPort.rateWork(eq(WORK_ID), eq(USER_ID), eq(rating), any(LocalDateTime.class)))
            .thenReturn(rating);
    }

    private double whenRatingWork(double ratingValue) {
        return rateWork.execute(WORK_ID, USER_ID, ratingValue);
    }

    private void thenRatingReturned(double actual, double expected) {
        assertEquals(expected, actual);
    }

    private void thenPortInvokedWith(double ratingValue) {
        verify(ratingPort).rateWork(eq(WORK_ID), eq(USER_ID), eq(ratingValue), any(LocalDateTime.class));
    }

    private void thenRatingFailsDueToInvalidValue(double ratingValue) {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> rateWork.execute(WORK_ID, USER_ID, ratingValue)
        );
        assertEquals("El rating debe estar entre 0.5 y 5.0 en incrementos de 0.5", exception.getMessage());
    }

    private void thenPortNotInvoked() {
        verifyNoInteractions(ratingPort);
    }
}
