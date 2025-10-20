package com.amool.application.port.out;

import com.amool.domain.model.Work;

import java.util.List;

public interface WorkPort {

     Long createWork(Work work);

     Boolean updateWork(Work work);
     
     List<Work> getAllWorks();

}
