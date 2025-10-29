package com.amool.adapters.in.rest.dtos;

import java.util.Date;
import java.util.List;

public class WorkResponseDto {
    Long id;
    String title;
    String description;
    String cover;
    String banner;
    String state;
    Date publicationDate;
    Double price;
    Integer likes;
    CreatorDto creator;
    FormatDto format;
    LanguageDto originalLanguage;
    List<ChapterDto> chapters;
    List<CategoryDto> categories;
    List<TagDto> tags;

    Boolean subscribedToAuthor;
    Boolean subscribedToWork;
    List<Long> unlockedChapters;

    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getCover() {
        return cover;
    }
    public String getBanner() {
        return banner;
    }
    public String getState() {
        return state;
    }
    public Date getPublicationDate() {
        return publicationDate;
    }
    public Double getPrice() {
        return price;
    }
    public Integer getLikes() {
        return likes;
    }
    public CreatorDto getCreator() {
        return creator;
    }
    public FormatDto getFormat() {
        return format;
    }
    public LanguageDto getOriginalLanguage() {
        return originalLanguage;
    }
    public List<ChapterDto> getChapters() {
        return chapters;
    }
    public List<CategoryDto> getCategories() {
        return categories;
    }
    public List<TagDto> getTags() {
        return tags;
    }
    public Boolean getSubscribedToAuthor() { return subscribedToAuthor; }
    public Boolean getSubscribedToWork() { return subscribedToWork; }
    public List<Long> getUnlockedChapters() { return unlockedChapters; }
    public void setId(Long id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }
    public void setBanner(String banner) {
        this.banner = banner;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public void setLikes(Integer likes) {
        this.likes = likes;
    }
    public void setCreator(CreatorDto creator) {
        this.creator = creator;
    }
    public void setFormat(FormatDto format) {
        this.format = format;
    }
    public void setOriginalLanguage(LanguageDto originalLanguage) {
        this.originalLanguage = originalLanguage;
    }
    public void setChapters(List<ChapterDto> chapters) {
        this.chapters = chapters;
    }
    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }
    
    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }
    public void setSubscribedToAuthor(Boolean subscribedToAuthor) { this.subscribedToAuthor = subscribedToAuthor; }
    public void setSubscribedToWork(Boolean subscribedToWork) { this.subscribedToWork = subscribedToWork; }
    public void setUnlockedChapters(List<Long> unlockedChapters) { this.unlockedChapters = unlockedChapters; }
}
