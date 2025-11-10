package com.amool.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Chapter {
    private Long id;
    private String title;
    private BigDecimal price;
    private Long likes;
    private LocalDateTime lastModified;
    private Long workId;
    private Long languageId; 
    private Boolean allowAiTranslation;
    private String publicationStatus;
    private LocalDateTime scheduledPublicationDate;
    private LocalDateTime publishedAt;
    private transient Boolean likedByUser;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Long getLikes() { return likes; }
    public void setLikes(Long likes) { this.likes = likes; }

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    
    public Long getLanguageId() { return languageId; }
    public void setLanguageId(Long languageId) { this.languageId = languageId; }
    
    public Boolean getAllowAiTranslation() { return allowAiTranslation; }
    public void setAllowAiTranslation(Boolean allowAiTranslation) { this.allowAiTranslation = allowAiTranslation; }

    public String getPublicationStatus() { return publicationStatus; }
    public void setPublicationStatus(String publicationStatus) { this.publicationStatus = publicationStatus; }

    public LocalDateTime getScheduledPublicationDate() { return scheduledPublicationDate; }
    public void setScheduledPublicationDate(LocalDateTime scheduledPublicationDate) { this.scheduledPublicationDate = scheduledPublicationDate; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public Boolean getLikedByUser() { return likedByUser; }
    public void setLikedByUser(Boolean likedByUser) { this.likedByUser = likedByUser; }
}
