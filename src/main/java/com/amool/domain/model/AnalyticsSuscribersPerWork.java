package com.amool.domain.model;

import java.time.LocalDateTime;

public class AnalyticsSuscribersPerWork {
    private Long userId;
    private Long workId;
    private LocalDateTime suscribedAt;

    public AnalyticsSuscribersPerWork(Long userId, Long workId, LocalDateTime suscribedAt) {
        this.userId = userId;
        this.workId = workId;
        this.suscribedAt = suscribedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public LocalDateTime getSuscribedAt() {
        return suscribedAt;
    }

    public void setSuscribedAt(LocalDateTime suscribedAt) {
        this.suscribedAt = suscribedAt;
    }
}
