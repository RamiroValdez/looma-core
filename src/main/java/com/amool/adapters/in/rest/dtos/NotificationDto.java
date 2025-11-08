package com.amool.adapters.in.rest.dtos;

import java.time.LocalDateTime;

import com.amool.domain.model.Notification.NotificationType;

public class NotificationDto {
    Long id;
    String message;
    boolean read;
    LocalDateTime createdAt;
    NotificationType type;
    String relatedWork;
    String relatedChapter;
    String relatedUser;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    
    public String getRelatedWork() { return relatedWork; }
    public void setRelatedWork(String relatedWork) { this.relatedWork = relatedWork; }
    
    public String getRelatedChapter() { return relatedChapter; }
    public void setRelatedChapter(String relatedChapter) { this.relatedChapter = relatedChapter; }
    
    public String getRelatedUser() { return relatedUser; }
    public void setRelatedUser(String relatedUser) { this.relatedUser = relatedUser; }
    
    
}
