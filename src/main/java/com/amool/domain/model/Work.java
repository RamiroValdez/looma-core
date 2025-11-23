package com.amool.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Work {
    private Long id;
    private String title;
    private String description;
    private String cover;
    private String banner;
    private String state;
    private BigDecimal price;
    private Integer likes;
    private Double averageRating;
    private Integer ratingCount;
    private LocalDate publicationDate;
    private User creator;
    private Format format;
    private Language originalLanguage;
    private Boolean hasEpub;
    private Boolean hasPdf;
    private Integer lengthPdf;
    private List<Chapter> chapters = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private Set<Tag> tags = new HashSet<>();
    private transient Boolean likedByUser;
    private Integer lengthEpub;

    public Work() {}

    public static Work initialize(String title, String description, BigDecimal price) {
        Work work = new Work();
        work.setTitle(title);
        work.setDescription(description);
        work.setCover("none");
        work.setBanner("none");
        work.setState("InProgress");
        work.setPrice(price == null ? BigDecimal.ZERO : price);
        work.setLikes(0);
        work.setHasEpub(false);
        work.setHasPdf(false);
        work.setPublicationDate(LocalDate.now());
        return work;
    }

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
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }
    
    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
    
    public Boolean getHasPdf() { return hasPdf; }
    public void setHasPdf(Boolean hasPdf) { this.hasPdf = hasPdf; }

    public Integer getLengthPdf() { return lengthPdf; }
    public void setLengthPdf(Integer lengthPdf) { this.lengthPdf = lengthPdf; }
    
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
    
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }
    
    public Boolean getLikedByUser() { return likedByUser; }
    public void setLikedByUser(Boolean likedByUser) { this.likedByUser = likedByUser; }

    public Boolean getHasEpub() { return hasEpub; }
    public void setHasEpub(Boolean hasEpub) { this.hasEpub = hasEpub; }

    public Integer getLengthEpub() { return lengthEpub; }
    public void setLengthEpub(Integer lengthEpub) { this.lengthEpub = lengthEpub; } 
}
