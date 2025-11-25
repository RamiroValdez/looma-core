package com.amool.application.usecases;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Work;

import java.util.List;

public class GetWorksByUserId {

    private final ObtainWorkByIdPort obtainWorkByIdPort;

    public GetWorksByUserId(ObtainWorkByIdPort obtainWorkByIdPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    public List<Work> execute(Long userId) {
        return obtainWorkByIdPort.getWorksByUserId(userId);
    }

}
