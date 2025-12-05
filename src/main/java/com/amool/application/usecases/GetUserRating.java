package com.amool.application.usecases;

import com.amool.application.port.out.RatingPort;

import java.util.Optional;

public class GetUserRating {

    private final RatingPort ratingPort;

    public GetUserRating(RatingPort ratingPort) {
        this.ratingPort = ratingPort;
    }

    public Optional<Double> execute(Long workId, Long userId) {
        return ratingPort.getUserRating(workId, userId);
    }
}
