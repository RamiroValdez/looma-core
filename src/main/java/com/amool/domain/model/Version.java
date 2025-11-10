package com.amool.domain.model;

public class Version {
    private Long id;
    private String content;
    private Long chapterId;
    private Long languageId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }

    public Long getLanguageId() { return languageId; }
    public void setLanguageId(Long languageId) { this.languageId = languageId; }
}
