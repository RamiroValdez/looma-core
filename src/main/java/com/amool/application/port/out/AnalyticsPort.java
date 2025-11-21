package com.amool.application.port.out;

import java.util.List;

import com.amool.domain.model.*;

public interface AnalyticsPort {
    public List<AnalyticsLikeWork> getLikesPerWork(Long workId);
    public List<AnalyticsLikeChapter> getLikesPerChapter(Long chapterId);
    public List<AnalyticsRatingWork> getRatingsPerWork(Long workId);
    public List<WorkSaved> getSavedWorks(Long workId);
    public Long getTotalPerAuthor(Long authorId);
    public Long getTotalPerWork(Long workId);
    public List<AnalyticsSuscribersPerWork> getSuscribersPerWork(Long workId);
    public List<AnalyticsSuscribersPerAuthor> getSuscribersPerAuthor(Long authorId);
    List<AnalyticsRetention> getRetentionTotalsPerChapter(Long workId);
    List<ReadingHistory> getReadingHistory(Long chapterId);
}
