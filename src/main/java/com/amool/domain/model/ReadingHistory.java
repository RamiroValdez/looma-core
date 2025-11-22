package com.amool.domain.model;

import com.amool.adapters.in.rest.dtos.AnalyticsReadingChapterDto;

import java.time.LocalDateTime;

public class ReadingHistory {

    private Long id;
    private Long userId;
    private Long workId;
    private Long chapterId;
    private LocalDateTime readAt;

    public ReadingHistory(Long id, Long userId, Long workId, Long chapterId, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.workId = workId;
        this.chapterId = chapterId;
        this.readAt = readAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public AnalyticsReadingChapterDto toDto() {
        return new AnalyticsReadingChapterDto(
            this.id,
            this.chapterId,
            this.readAt,
            this.userId,
            this.workId
        );
    }
}
