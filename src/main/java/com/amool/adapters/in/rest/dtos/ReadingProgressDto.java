package com.amool.adapters.in.rest.dtos;

public class ReadingProgressDto {
    private Long userId;
    private Long workId;
    private Long chapterId;
    
    public ReadingProgressDto(Long userId, Long workId, Long chapterId) {
        this.userId = userId;
        this.workId = workId;
        this.chapterId = chapterId;
    }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    
}
