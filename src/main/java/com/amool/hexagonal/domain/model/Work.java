package com.amool.hexagonal.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Work {
    private Long id;
    private String title;
    private String description;
    private String cover;
    private String banner;
    private String state;
    private Double price;
    private Integer likes;
    private LocalDate publicationDate;
    private User creator;
    private Format format;
    private Language originalLanguage;
    private List<Chapter> chapters = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();

    public Work() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    
    public String getBanner() { return banner; }
    public void setBanner(String banner) { this.banner = banner; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    
    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
    
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    
    public Format getFormat() { return format; }
    public void setFormat(Format format) { this.format = format; }

    public Language getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(Language originalLanguage) { this.originalLanguage = originalLanguage; }
    
    public List<Chapter> getChapters() { return chapters; }
    public void setChapters(List<Chapter> chapters) { this.chapters = chapters; }
    
    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }
    
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
}
