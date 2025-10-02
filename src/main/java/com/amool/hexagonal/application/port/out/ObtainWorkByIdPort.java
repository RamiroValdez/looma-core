package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.Work;
import java.util.List;

public interface ObtainWorkByIdPort {
    public Work execute(Long workId);
    public List<Work> getWorksByUserId(Long userId);
}
