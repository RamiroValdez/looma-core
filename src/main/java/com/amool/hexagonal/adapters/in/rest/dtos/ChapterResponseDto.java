package com.amool.hexagonal.adapters.in.rest.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class ChapterResponseDto {
    Long id;
    String title;
    String content;
    Double price;
    String work_name;
    LocalDateTime last_update;
    Long likes;
    Boolean allowAiTranslation;
    String language_code;
    String publicationStatus;
    LocalDateTime scheduledPublicationDate;
    LocalDateTime publishedAt;
    List<String> availableLanguages;
    Integer chapter_number;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getWorkName() { return work_name; }
    public void setWorkName(String work_name) { this.work_name = work_name; }

    public LocalDateTime getLastUpdate() { return last_update; }
    public void setLastUpdate(LocalDateTime last_update) { this.last_update = last_update; }

    public Long getLikes() { return likes; }
    public void setLikes(Long likes) { this.likes = likes; }

    public Boolean getAllowAiTranslation() { return allowAiTranslation; }
    public void setAllowAiTranslation(Boolean allowAiTranslation) { this.allowAiTranslation = allowAiTranslation; }

    public String getLanguageCode() { return language_code; }
    public void setLanguageCode(String language_code) { this.language_code = language_code; }

    public String getPublicationStatus() { return publicationStatus; }
    public void setPublicationStatus(String publicationStatus) { this.publicationStatus = publicationStatus; }

    public LocalDateTime getScheduledPublicationDate() { return scheduledPublicationDate; }
    public void setScheduledPublicationDate(LocalDateTime scheduledPublicationDate) { this.scheduledPublicationDate = scheduledPublicationDate; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public List<String> getAvailableLanguages() { return availableLanguages; }
    public void setAvailableLanguages(List<String> availableLanguages) { this.availableLanguages = availableLanguages; }

    public Integer getChapterNumber() { return chapter_number; }
    public void setChapterNumber(Integer chapter_number) { this.chapter_number = chapter_number; }

}
