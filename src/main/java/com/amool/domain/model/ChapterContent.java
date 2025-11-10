package com.amool.domain.model;

import java.util.Map;

public class ChapterContent {
    private final String workId;
    private final String chapterId;
    private final Map<String, String> contentByLanguage;
    private final String defaultLanguage;

    public ChapterContent(String workId, String chapterId, Map<String, String> contentByLanguage, String defaultLanguage) {
        this.workId = workId;
        this.chapterId = chapterId;
        this.contentByLanguage = contentByLanguage != null ? contentByLanguage : Map.of();
        this.defaultLanguage = defaultLanguage;
    }

    public String getWorkId() {
        return workId;
    }
    
    public String getChapterId() {
        return chapterId;
    }

    public Map<String, String> getContentByLanguage() {
        return contentByLanguage;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getContent(String language) {
        return contentByLanguage.getOrDefault(language, 
                contentByLanguage.getOrDefault(defaultLanguage, ""));
    }
}
