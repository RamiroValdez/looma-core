package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;
import java.util.List;
import com.amool.domain.model.AnalyticsLikeChapter;

public class GetLikesPerChapterUseCase {
    AnalyticsPort analyticsPort;

    public GetLikesPerChapterUseCase(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public List<AnalyticsLikeChapter> execute(Long chapterId) {
        return analyticsPort.getLikesPerChapter(chapterId);
    }
}
