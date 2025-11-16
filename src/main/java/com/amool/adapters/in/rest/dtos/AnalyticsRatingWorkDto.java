package com.amool.adapters.in.rest.dtos;

import java.time.LocalDateTime;

public record AnalyticsRatingWorkDto(
    Long ratingId, 
    Double rating, 
    Long userId, 
    Long workId, 
    LocalDateTime ratedAt) {
    
}
