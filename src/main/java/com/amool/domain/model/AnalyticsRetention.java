package com.amool.domain.model;

import com.amool.adapters.in.rest.dtos.AnalyticsRetentionDto;

public class AnalyticsRetention {
    
    private Long chapter;
    private Long totalReaders;
    private Double percentFromFirst;
    private Double percentFromPrevious;

    public AnalyticsRetention() {}

    public AnalyticsRetention(long chapter, long totalReaders, double percentFromFirst, double percentFromPrevious) {
        this.chapter = chapter;
        this.totalReaders = totalReaders;
        this.percentFromFirst = percentFromFirst;
        this.percentFromPrevious = percentFromPrevious;
    }

    public long getChapter() { return chapter; }
    public long getTotalReaders() { return totalReaders; }
    public Double getPercentFromFirst() { return percentFromFirst; }
    public Double getPercentFromPrevious() { return percentFromPrevious; }

    public void setChapter(Long chapter) {
        this.chapter = chapter;
    }

    public void setTotalReaders(Long totalReaders) {
        this.totalReaders = totalReaders;
    }

    public void setPercentFromFirst(double percentFromFirst) {
        this.percentFromFirst = percentFromFirst;
    }

    public void setPercentFromPrevious(double percentFromPrevious) {
        this.percentFromPrevious = percentFromPrevious;
    }

    public AnalyticsRetentionDto toDto(){
        AnalyticsRetentionDto dto = new AnalyticsRetentionDto(this.chapter, this.totalReaders, this.percentFromFirst, this.percentFromPrevious);
        return dto;
    }
}
