package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;
import java.util.List;
import com.amool.domain.model.AnalyticsLikeChapter;

public class GetLikesPerChapter {
    AnalyticsPort analyticsPort;

    public GetLikesPerChapter(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public List<AnalyticsLikeChapter> execute(Long chapterId) {
        return analyticsPort.getLikesPerChapter(chapterId);
    }
}
