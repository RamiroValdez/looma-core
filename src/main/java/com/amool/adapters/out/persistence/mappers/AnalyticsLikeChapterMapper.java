package com.amool.adapters.out.persistence.mappers;

import com.amool.adapters.out.persistence.entity.ChapterLikeEntity;
import com.amool.domain.model.AnalyticsLikeChapter;

public class AnalyticsLikeChapterMapper {
    
    public static AnalyticsLikeChapter toDomain(ChapterLikeEntity chapterLikeEntity){
        return new AnalyticsLikeChapter(
            chapterLikeEntity.getId(),
            chapterLikeEntity.getChapter().getId(),
            chapterLikeEntity.getUser().getId(),
            chapterLikeEntity.getLikedAt()
        );
    }
}
