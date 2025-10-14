package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.Work;
import java.util.Optional;
import java.util.List;

public interface ObtainWorkByIdPort {
    public Optional<Work> obtainWorkById(Long workId);

    public List<Work> getWorksByUserId(Long userId);
}
