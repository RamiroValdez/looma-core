package com.amool.application.usecases;

import com.amool.application.port.out.RatingPort;

import java.util.Optional;

public class GetUserRatingUseCase {

    private final RatingPort ratingPort;

    public GetUserRatingUseCase(RatingPort ratingPort) {
        this.ratingPort = ratingPort;
    }

    public Optional<Double> execute(Long workId, Long userId) {
        return ratingPort.getUserRating(workId, userId);
    }
}
