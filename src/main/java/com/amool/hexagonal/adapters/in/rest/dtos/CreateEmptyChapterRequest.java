package com.amool.hexagonal.adapters.in.rest.dtos;

public class CreateEmptyChapterRequest {
    private Long workId;  
    private String contentType; 
    private Long languageId;  

    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getLanguageId() { return languageId; }
    public void setLanguageId(Long languageId) { this.languageId = languageId; }
}
