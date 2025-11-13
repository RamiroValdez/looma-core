package com.amool.application.usecases;

import com.amool.application.port.out.AnalyticsPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Work;
import java.util.List;

public class GetTotalSuscribersUseCase {
    AnalyticsPort analyticsPort;
    ObtainWorkByIdPort obtainWorkByIdPort;

    public GetTotalSuscribersUseCase(AnalyticsPort analyticsPort, ObtainWorkByIdPort obtainWorkByIdPort) {
        this.analyticsPort = analyticsPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    public Long execute(Long authorId){
        List<Work> works = obtainWorkByIdPort.getWorksByUserId(authorId);
        Long totalSuscribersPerWork = works.stream().map(work -> analyticsPort.getTotalPerWork(work.getId())).reduce(0L, Long::sum);
        Long totalSuscribersPerAuthor = analyticsPort.getTotalPerAuthor(authorId);
        return totalSuscribersPerWork + totalSuscribersPerAuthor;
    }
}
