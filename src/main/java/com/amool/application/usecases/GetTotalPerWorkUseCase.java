package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;

public class GetTotalPerWorkUseCase {
    AnalyticsPort analyticsPort;

    public GetTotalPerWorkUseCase(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public Long execute(Long workId){
        return analyticsPort.getTotalPerWork(workId);
    }
}
