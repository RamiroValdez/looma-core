package com.amool.domain.model;

import java.time.LocalDateTime;

public class AnalyticsRatingWork {
    private Long ratingId;
    private Double rating;
    private Long userId;
    private Long workId;
    private LocalDateTime ratedAt;

    public AnalyticsRatingWork(Long ratingId, Double rating, Long userId, Long workId, LocalDateTime ratedAt) {
        this.ratingId = ratingId;
        this.rating = rating;
        this.userId = userId;
        this.workId = workId;
        this.ratedAt = ratedAt;
    }

    public Long getRatingId() {
        return ratingId;
    }

    public Double getRating() {
        return rating;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getWorkId() {
        return workId;
    }

    public LocalDateTime getRatedAt() {
        return ratedAt;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public void setRatedAt(LocalDateTime ratedAt) {
        this.ratedAt = ratedAt;
    }

}

