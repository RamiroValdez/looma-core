package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.Work;

public interface ObtainWorkByIdPort {
    public Work execute(Long workId);
}
