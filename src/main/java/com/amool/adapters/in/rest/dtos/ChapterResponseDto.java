package com.amool.adapters.in.rest.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ChapterResponseDto {
    Long id;
    String title;
    String content;
    BigDecimal price;
    String work_name;
    String work_id;
    LocalDateTime last_update;
    Long likes;
    Boolean allowAiTranslation;
    LanguageDto languageDefaultCode;
    String publicationStatus;
    LocalDateTime scheduledPublicationDate;
    LocalDateTime publishedAt;
    List<LanguageDto> availableLanguages;
    Integer chapterNumber;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getWorkName() { return work_name; }
    public void setWorkName(String work_name) { this.work_name = work_name; }

    public String getWorkId() { return work_id; }
    public void setWorkId(String work_id) { this.work_id = work_id; }

    public LocalDateTime getLastUpdate() { return last_update; }
    public void setLastUpdate(LocalDateTime last_update) { this.last_update = last_update; }

    public Long getLikes() { return likes; }
    public void setLikes(Long likes) { this.likes = likes; }

    public Boolean getAllowAiTranslation() { return allowAiTranslation; }
    public void setAllowAiTranslation(Boolean allowAiTranslation) { this.allowAiTranslation = allowAiTranslation; }

    public LanguageDto getLanguageDefaultCode() { return languageDefaultCode; }
    public void setLanguageDefaultCode(LanguageDto language_code) { this.languageDefaultCode = language_code; }

    public String getPublicationStatus() { return publicationStatus; }
    public void setPublicationStatus(String publicationStatus) { this.publicationStatus = publicationStatus; }

    public LocalDateTime getScheduledPublicationDate() { return scheduledPublicationDate; }
    public void setScheduledPublicationDate(LocalDateTime scheduledPublicationDate) { this.scheduledPublicationDate = scheduledPublicationDate; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public List<LanguageDto> getAvailableLanguages() { return availableLanguages; }
    public void setAvailableLanguages(List<LanguageDto> availableLanguages) { this.availableLanguages = availableLanguages; }

    public Integer getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(Integer chapter_number) { this.chapterNumber = chapter_number; }

}
