package com.amool.application.usecases;

import java.util.List;

import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.AnalyticsRatingWork;

public class GetRatingsPerWork {
    AnalyticsPort analyticsPort;

    public GetRatingsPerWork(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public List<AnalyticsRatingWork> execute(Long workId) {
        return analyticsPort.getRatingsPerWork(workId);
    }
}
