package com.amool.application.port.out;

import com.amool.domain.model.Work;

public interface WorkPort {

     Long createWork(Work work);

     Boolean updateWork(Work work);

}
