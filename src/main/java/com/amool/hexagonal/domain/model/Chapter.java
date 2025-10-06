package com.amool.hexagonal.domain.model;

import java.time.LocalDateTime;

public class Chapter {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Long likes;
    private LocalDateTime lastModified;
    private Long bookId;
    private String publicationStatus;
    private LocalDateTime scheduledPublicationDate;
    private LocalDateTime publishedAt;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Long getLikes() { return likes; }
    public void setLikes(Long likes) { this.likes = likes; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getPublicationStatus() { return publicationStatus; }
    public void setPublicationStatus(String publicationStatus) { this.publicationStatus = publicationStatus; }

    public LocalDateTime getScheduledPublicationDate() { return scheduledPublicationDate; }
    public void setScheduledPublicationDate(LocalDateTime scheduledPublicationDate) { this.scheduledPublicationDate = scheduledPublicationDate; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
