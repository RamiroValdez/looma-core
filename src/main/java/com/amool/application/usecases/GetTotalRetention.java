package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.*;
import java.util.List;

public class GetTotalRetention {

    private AnalyticsPort analyticsPort;

    public GetTotalRetention(AnalyticsPort analyticsPort){
        this.analyticsPort = analyticsPort; 
    }

    public List<AnalyticsRetentionTotal> execute(Long workId){
        return this.analyticsPort.getRetentionTotalsPerChapter(workId);
    }

}
