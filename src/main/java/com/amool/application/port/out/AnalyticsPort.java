package com.amool.application.port.out;

import java.util.List;

import com.amool.domain.model.AnalyticsLikeChapter;
import com.amool.domain.model.AnalyticsLikeWork;
import com.amool.domain.model.AnalyticsRatingWork;
import com.amool.domain.model.AnalyticsRetentionTotal;
import com.amool.domain.model.WorkSaved;
import com.amool.domain.model.AnalyticsSuscribersPerWork;
import com.amool.domain.model.AnalyticsSuscribersPerAuthor;

public interface AnalyticsPort {
    public List<AnalyticsLikeWork> getLikesPerWork(Long workId);
    public List<AnalyticsLikeChapter> getLikesPerChapter(Long chapterId);
    public List<AnalyticsRatingWork> getRatingsPerWork(Long workId);
    public List<WorkSaved> getSavedWorks(Long workId);
    public Long getTotalPerAuthor(Long authorId);
    public Long getTotalPerWork(Long workId);
    public List<AnalyticsSuscribersPerWork> getSuscribersPerWork(Long workId);
    public List<AnalyticsSuscribersPerAuthor> getSuscribersPerAuthor(Long authorId);
    List<AnalyticsRetentionTotal> getRetentionTotalsPerChapter(Long workId);
}
