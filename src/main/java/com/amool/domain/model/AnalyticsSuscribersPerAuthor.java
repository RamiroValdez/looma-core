package com.amool.domain.model;

import java.time.LocalDateTime;

public class AnalyticsSuscribersPerAuthor {
    private Long userId;
    private Long authorId;
    private LocalDateTime suscribedAt;

    public AnalyticsSuscribersPerAuthor(Long userId, Long authorId, LocalDateTime suscribedAt) {
        this.userId = userId;
        this.authorId = authorId;
        this.suscribedAt = suscribedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getSuscribedAt() {
        return suscribedAt;
    }

    public void setSuscribedAt(LocalDateTime suscribedAt) {
        this.suscribedAt = suscribedAt;
    }
}
