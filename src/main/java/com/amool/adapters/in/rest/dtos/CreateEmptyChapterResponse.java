package com.amool.adapters.in.rest.dtos;

public class CreateEmptyChapterResponse {
    private Long chapterId;
    private String contentType;

    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}
