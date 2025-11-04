package com.amool.adapters.in.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RatingResponse {
    @JsonProperty("work_id")
    private final Long workId;
    
    @JsonProperty("user_id")
    private final Long userId;
    
    @JsonProperty("rating")
    private final Double rating;
    
    @JsonProperty("average_rating")
    private final Double averageRating;

    public RatingResponse(Long workId, Long userId, Double rating, Double averageRating) {
        this.workId = workId;
        this.userId = userId;
        this.rating = rating;
        this.averageRating = averageRating;
    }

    public Long getWorkId() {
        return workId;
    }

    public Long getUserId() {
        return userId;
    }

    public Double getRating() {
        return rating;
    }

    public Double getAverageRating() {
        return averageRating;
    }
}
