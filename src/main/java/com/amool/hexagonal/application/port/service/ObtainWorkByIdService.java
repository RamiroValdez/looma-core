package com.amool.hexagonal.application.port.service;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.application.port.in.ObtainWorkByIdUseCase;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Work;

public class ObtainWorkByIdService implements ObtainWorkByIdUseCase {

    private ObtainWorkByIdPort obtainWorkByIdPort;

    public ObtainWorkByIdService(ObtainWorkByIdPort obtainWorkByIdPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    @Override
    public WorkResponseDto execute(Long workId) {

        Work work = obtainWorkByIdPort.execute(workId);

        WorkResponseDto workdto = WorkMapper.toDto(work);

        return workdto;
    }

}
