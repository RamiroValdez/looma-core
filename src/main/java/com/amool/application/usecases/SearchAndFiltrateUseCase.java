package com.amool.application.usecases;

import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.WorkSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SearchAndFiltrateUseCase {

    private final WorkPort workPort;

    public SearchAndFiltrateUseCase(WorkPort workPort) {
        this.workPort = workPort;
    }

    public Page<Work> execute(WorkSearchFilter filter, Pageable pageable) {
        return workPort.findByFilters(filter, pageable);
    }

}
