package com.amool.hexagonal.application.port.in;
import com.amool.hexagonal.domain.model.Work;

public interface ObtainWorkByIdUseCase {

    Work execute(Long workId);

}
