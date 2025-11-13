package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.AnalyticsLikeChapterDto;
import com.amool.domain.model.AnalyticsLikeChapter;

public class AnalyticsLikeChapterMapper {
    
    public static AnalyticsLikeChapterDto toDto(AnalyticsLikeChapter analyticsLikeChapter){
        return new AnalyticsLikeChapterDto(
            analyticsLikeChapter.getLikeId(),
            analyticsLikeChapter.getChapterId(),
            analyticsLikeChapter.getUserId(),
            analyticsLikeChapter.getLikedAt()
        );
    }

    public static AnalyticsLikeChapter toDomain(AnalyticsLikeChapterDto analyticsLikeChapterDto){
        return new AnalyticsLikeChapter(
            analyticsLikeChapterDto.likeId(),
            analyticsLikeChapterDto.chapterId(),
            analyticsLikeChapterDto.userId(),
            analyticsLikeChapterDto.likedAt()
        );
    }
}
