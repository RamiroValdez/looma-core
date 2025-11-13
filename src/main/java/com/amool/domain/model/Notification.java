package com.amool.domain.model;

import java.time.LocalDateTime;

public class Notification {

    private Long id;
    private Long userId;
    private String message;
    private boolean read;
    private LocalDateTime createdAt; 
    private NotificationType type;

    private Long relatedWork;   
    private Long relatedChapter; 
    private Long relatedUser;

    public enum NotificationType {
        WORK_UPDATED,              
        NEW_WORK_PUBLISHED,     
        NEW_WORK_SUBSCRIBER,       
        NEW_AUTHOR_SUBSCRIBER,     
        NEW_CHAPTER_SUBSCRIBER    
    }

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
