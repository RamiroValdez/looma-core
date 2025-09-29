package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;

public interface ObtainWorkByIdUseCase {

    WorkResponseDto execute(Long workId);

}
