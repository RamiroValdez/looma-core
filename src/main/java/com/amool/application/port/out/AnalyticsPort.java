package com.amool.application.port.out;

import java.util.List;

import com.amool.domain.model.AnalyticsLikeChapter;
import com.amool.domain.model.AnalyticsLikeWork;
import com.amool.domain.model.AnalyticsRatingWork;

public interface AnalyticsPort {
    public List<AnalyticsLikeWork> getLikesPerWork(Long workId);
    public List<AnalyticsLikeChapter> getLikesPerChapter(Long chapterId);
    public List<AnalyticsRatingWork> getRatingsPerWork(Long workId);
}
