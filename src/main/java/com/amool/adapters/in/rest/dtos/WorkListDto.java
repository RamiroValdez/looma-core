package com.amool.adapters.in.rest.dtos;

import java.util.List;

public class WorkListDto {

    Long id;
    String title;
    String description;
    String cover;
    Integer likes;
    FormatDto format;
    List<CategoryDto> categories;

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
    
    public Integer getLikes() {
        return likes;
    }
    
    public FormatDto getFormat() {
        return format;
    }
    
    public List<CategoryDto> getCategories() {
        return categories;
    }

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
   
    public void setLikes(Integer likes) {
        this.likes = likes;
    }
    
    public void setFormat(FormatDto format) {
        this.format = format;
    }
    
    
    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }
    
}
    
