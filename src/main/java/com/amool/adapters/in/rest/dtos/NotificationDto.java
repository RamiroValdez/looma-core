package com.amool.adapters.in.rest.dtos;

import java.time.LocalDateTime;

import com.amool.domain.model.Notification.NotificationType;

public class NotificationDto {
    Long id;
    Long userId;
    String message;
    boolean read;
    LocalDateTime createdAt;
    NotificationType type;
    Long relatedWork;
    Long relatedChapter;
    Long relatedUser;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    
    public Long getRelatedWork() { return relatedWork; }
    public void setRelatedWork(Long relatedWork) { this.relatedWork = relatedWork; }
    
    public Long getRelatedChapter() { return relatedChapter; }
    public void setRelatedChapter(Long relatedChapter) { this.relatedChapter = relatedChapter; }
    
    public Long getRelatedUser() { return relatedUser; }
    public void setRelatedUser(Long relatedUser) { this.relatedUser = relatedUser; }
    
    
}
