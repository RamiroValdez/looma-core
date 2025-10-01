package com.amool.hexagonal.application.port.service;

import com.amool.hexagonal.application.port.in.ObtainWorkByIdUseCase;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Work;
import org.springframework.stereotype.Service;

@Service
public class ObtainWorkByIdService implements ObtainWorkByIdUseCase {

    private ObtainWorkByIdPort obtainWorkByIdPort;

    public ObtainWorkByIdService(ObtainWorkByIdPort obtainWorkByIdPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    @Override
    public Work execute(Long workId) {
        return obtainWorkByIdPort.execute(workId);
    }

}
