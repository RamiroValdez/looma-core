package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;

public class GetTotalPerAuthorUseCase {
    AnalyticsPort analyticsPort;

    public GetTotalPerAuthorUseCase(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public Long execute(Long authorId){
        return analyticsPort.getTotalPerAuthor(authorId);
    }
}
