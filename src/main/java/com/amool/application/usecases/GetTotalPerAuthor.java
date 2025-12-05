package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;

public class GetTotalPerAuthor {
    AnalyticsPort analyticsPort;

    public GetTotalPerAuthor(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public Long execute(Long authorId){
        return analyticsPort.getTotalPerAuthor(authorId);
    }
}
