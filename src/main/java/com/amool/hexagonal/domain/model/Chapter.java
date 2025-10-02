package com.amool.hexagonal.domain.model;

import java.time.LocalDateTime;

public class Chapter {
    private Long id;
    private String title;
    private Double price;
    private Long likes;
    private LocalDateTime lastModified;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Long getLikes() { return likes; }
    public void setLikes(Long likes) { this.likes = likes; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
}
