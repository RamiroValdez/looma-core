package com.amool.domain.model;

import java.time.LocalDateTime;

public class WorkSaved {
    private Long id;
    private Long userId;
    private Long workId;
    private LocalDateTime savedAt;

    public WorkSaved(Long id, Long userId, Long workId, LocalDateTime savedAt) {
        this.id = id;
        this.userId = userId;
        this.workId = workId;
        this.savedAt = savedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
}
