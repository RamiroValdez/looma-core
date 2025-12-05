package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.AnalyticsLikeWork;
import java.util.List;

public class GetLikesPerWork {
    AnalyticsPort analyticsPort;

    public GetLikesPerWork(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public List<AnalyticsLikeWork> execute(Long workId) {
        return analyticsPort.getLikesPerWork(workId);
    }
}
