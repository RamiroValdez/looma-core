package com.amool.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RatingPort {
   
    double rateWork(Long workId, Long userId, double rating);

    Optional<Double> getUserRating(Long workId, Long userId);
    
    Double getAverageRating(Long workId);
    
    Page<RatingDto> getWorkRatings(Long workId, Pageable pageable);
    
    record RatingDto(Long userId, Double rating) {}
}
