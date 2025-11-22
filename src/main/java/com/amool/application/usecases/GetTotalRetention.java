package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.*;

import java.util.ArrayList;
import java.util.List;

public class GetTotalRetention {

    private AnalyticsPort analyticsPort;

    public GetTotalRetention(AnalyticsPort analyticsPort){
        this.analyticsPort = analyticsPort; 
    }

    public List<AnalyticsRetention> execute(Long workId){
        List<AnalyticsRetention> totalRetention = this.analyticsPort.getRetentionTotalsPerChapter(workId);

        List<AnalyticsRetention> retentiontOrdered = new ArrayList<>();

        if (totalRetention.isEmpty()){
            return retentiontOrdered;
        }

        long firstReaders = totalRetention.get(0).getTotalReaders();
        long previousReaders = 0;

        for (AnalyticsRetention retention : totalRetention){
            double percentFromFirst = ((double) retention.getTotalReaders() / firstReaders) * 100;
            double percentFromPrevious = previousReaders == 0 ? 100.0 : ((double) retention.getTotalReaders() / previousReaders) * 100;
            AnalyticsRetention retentionWithPercentages = new AnalyticsRetention(
                retention.getChapter(),
                retention.getTotalReaders(),
                percentFromFirst,
                percentFromPrevious
            );
            retentiontOrdered.add(retentionWithPercentages);
            previousReaders = retention.getTotalReaders();
        }

        return retentiontOrdered;
    }

}
