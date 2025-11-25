package com.amool.application.usecases;

import java.util.List;

import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.WorkSaved;

public class GetSavesPerWork {
    AnalyticsPort analyticsPort;

    public GetSavesPerWork(AnalyticsPort analyticsPort) {
        this.analyticsPort = analyticsPort;
    }

    public List<WorkSaved> execute(Long workId) {
        return analyticsPort.getSavedWorks(workId);
    }
}
