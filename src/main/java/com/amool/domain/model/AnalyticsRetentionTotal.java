package com.amool.domain.model;

import com.amool.adapters.in.rest.dtos.AnalyticsRetentionTotalDto;

public class AnalyticsRetentionTotal {
    
    private Long chapter;
    private Long totalReaders;

    public AnalyticsRetentionTotal(){

    }

    public Long getChapter() {
        return chapter;
    }

    public void setChapter(Long chapter) {
        this.chapter = chapter;
    }

    public Long getTotalReaders() {
        return totalReaders;
    }

    public void setTotalReaders(Long totalReaders) {
        this.totalReaders = totalReaders;
    }

    public AnalyticsRetentionTotalDto toDto(){
        AnalyticsRetentionTotalDto dto = new AnalyticsRetentionTotalDto(this.chapter, this.totalReaders);
        return dto;
    }
}
