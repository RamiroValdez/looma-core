package com.amool.domain.model;

import java.time.LocalDateTime;

public class AnalyticsLikeWork {
    private Long likeId;
    private Long workId;
    private Long userId;
    private LocalDateTime likedAt;

    public AnalyticsLikeWork(Long likeId, Long workId, Long userId, LocalDateTime likedAt) {
        this.likeId = likeId;
        this.workId = workId;
        this.userId = userId;
        this.likedAt = likedAt;
    }

    public Long setLikeId(Long likeId) {
        this.likeId = likeId;
        return likeId;
    }

    public Long setWorkId(Long workId) {
        this.workId = workId;
        return workId;
    }

    public Long setUserId(Long userId) {
        this.userId = userId;
        return userId;
    }

    public LocalDateTime setLikedAt(LocalDateTime likedAt) {
        this.likedAt = likedAt;
        return likedAt;
    }

    public LocalDateTime getLikedAt() {
        return likedAt;
    }

    public Long getLikeId() {
        return likeId;
    }

    public Long getWorkId() {
        return workId;
    }

    public Long getUserId() {
        return userId;
    }
}
