package com.amool.adapters.in.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RatingListResponse(
    @JsonProperty("work_id") Long workId,
    @JsonProperty("average_rating") Double averageRating,
    @JsonProperty("total_ratings") Long totalRatings,
    @JsonProperty("ratings") List<RatingDto> ratings
) {
    public record RatingDto(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("rating") Double rating
    ) {}
}
