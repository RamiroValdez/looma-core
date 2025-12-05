package com.amool.application.usecases;

import java.util.List;

import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.AnalyticsSuscribersPerWork;

public class GetSuscribersPerWork {
    AnalyticsPort analyticsPort;

    public GetSuscribersPerWork(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public List<AnalyticsSuscribersPerWork> execute(Long workId){
        return analyticsPort.getSuscribersPerWork(workId);
    }
}
