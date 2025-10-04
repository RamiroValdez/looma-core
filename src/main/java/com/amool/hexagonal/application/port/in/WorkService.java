package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Work;
import java.util.Optional;
import java.util.List;

public interface WorkService {

    Optional<Work> obtainWorkById(Long workId);

    List<Work> getWorksByUserId(Long userId);
    
    List<Work> getAuthenticatedUserWorks(Long authenticatedUserId);

    default Optional<Work> getById(Long workId) {
        return obtainWorkById(workId);
    }

    @Deprecated
    default List<Work> getByCreatorId(Long userId) {
        return getWorksByUserId(userId);
    }
}
