package com.amool.application.usecases;

import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Work;

import java.util.List;

public class GetAllWorksUseCase {

    private final WorkPort workPort;

    public GetAllWorksUseCase(WorkPort workPort) {
        this.workPort = workPort;
    }

    public List<Work> execute() {
        return workPort.getAllWorks();
    }
}
