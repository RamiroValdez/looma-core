package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Work;
import java.util.List;

public interface ObtainWorkByIdUseCase {

    Work execute(Long workId);
    List<Work> getWorksByUserId(Long userId);

}
