package com.amool.application.usecases;

import java.util.List;

import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.AnalyticsSuscribersPerAuthor;

public class GetSuscribersPerAuthor {
    AnalyticsPort analyticsPort;

    public GetSuscribersPerAuthor(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public List<AnalyticsSuscribersPerAuthor> execute(Long authorId){
        return analyticsPort.getSuscribersPerAuthor(authorId);
    }
}
