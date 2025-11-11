package com.amool.application.port.out;

import java.util.List;

import com.amool.domain.model.AnalyticsLikeWork;

public interface AnalyticsPort {
    public List<AnalyticsLikeWork> getLikesPerWork(Long workId);
}
