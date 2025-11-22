package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.ReadingHistory;

import java.util.List;

public class ObtainReadingHistory {

    AnalyticsPort analyticsPort;

    public ObtainReadingHistory(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public List<ReadingHistory> execute(Long chapterId) {
        return analyticsPort.getReadingHistory(chapterId);
    }

}
