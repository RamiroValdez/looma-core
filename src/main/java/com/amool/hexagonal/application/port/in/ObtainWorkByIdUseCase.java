package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Work;
import java.util.Optional;
import java.util.List;

public interface ObtainWorkByIdUseCase {

    Optional<Work> execute(Long workId);

    List<Work> getWorksByUserId(Long userId);

    // Clearer names (preferred going forward)
    default Optional<Work> getById(Long workId) {
        return execute(workId);
    }

    default List<Work> getByCreatorId(Long userId) {
        return getWorksByUserId(userId);
    }
}
