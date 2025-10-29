package com.amool.adapters.in.rest.dtos;

public class ChatRequestDto {
    private Long chapterId;
    private String message;
    private String chapterContent;
    
    public Long getChapterId() {
        return chapterId;
    }
    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getChapterContent() {
        return chapterContent;
    }
    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }
}
