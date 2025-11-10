package com.amool.application.port.out;

import com.amool.domain.model.Work;
import com.amool.domain.model.WorkSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkPort {

     Long createWork(Work work);

     Boolean updateWork(Work work);
     
     List<Work> getAllWorks();

     List<Work> getWorksCurrentlyReading(Long userId);

    public Page<Work> findByFilters(WorkSearchFilter filter, Pageable pageable);
}
