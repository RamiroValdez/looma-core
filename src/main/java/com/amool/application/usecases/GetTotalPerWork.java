package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;

public class GetTotalPerWork {
    AnalyticsPort analyticsPort;

    public GetTotalPerWork(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public Long execute(Long workId){
        return analyticsPort.getTotalPerWork(workId);
    }
}
