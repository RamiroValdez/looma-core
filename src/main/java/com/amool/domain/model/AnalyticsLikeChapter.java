package com.amool.domain.model;

import java.time.LocalDateTime;

public class AnalyticsLikeChapter {
    private Long likeId;
    private Long chapterId;
    private Long userId;
    private LocalDateTime likedAt;

    public AnalyticsLikeChapter(Long likeId, Long chapterId, Long userId, LocalDateTime likedAt) {
        this.likeId = likeId;
        this.chapterId = chapterId;
        this.userId = userId;
        this.likedAt = likedAt;
    }

    public Long getLikeId() {
        return likeId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getLikedAt() {
        return likedAt;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setLikedAt(LocalDateTime likedAt) {
        this.likedAt = likedAt;
    }
}
