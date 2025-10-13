package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.Work;

import java.util.Optional;

public interface WorkPort {

     Long createWork(Work work);

     Boolean updateWork(Work work);
     
     Boolean deleteWork(Long workId);
}
