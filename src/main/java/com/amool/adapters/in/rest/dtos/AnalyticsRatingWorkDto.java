package com.amool.adapters.in.rest.dtos;

import org.threeten.bp.LocalDateTime;

public record AnalyticsRatingWorkDto(
    Long ratingId, 
    Double rating, 
    Long userId, 
    Long workId, 
    LocalDateTime ratedAt) {
    
}
