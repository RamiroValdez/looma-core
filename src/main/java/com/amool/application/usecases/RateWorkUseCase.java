package com.amool.application.usecases;

import java.time.LocalDateTime;

import com.amool.application.port.out.RatingPort;
import com.amool.domain.model.Rating;

public class RateWorkUseCase {

    private final RatingPort ratingPort;

    public RateWorkUseCase(RatingPort ratingPort) {
        this.ratingPort = ratingPort;
    }

    public double execute(Long workId, Long userId, double ratingValue) {
        if (!Rating.isValid(ratingValue)) {
            throw new IllegalArgumentException("El rating debe estar entre 0.5 y 5.0 en incrementos de 0.5");
        }
        LocalDateTime createdAt = LocalDateTime.now();
        return ratingPort.rateWork(workId, userId, ratingValue, createdAt);
    }
}
