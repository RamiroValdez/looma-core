package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Work;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WorkServiceImpl implements WorkService {

    private final ObtainWorkByIdPort obtainWorkByIdPort;

    public WorkServiceImpl(ObtainWorkByIdPort obtainWorkByIdPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Work> execute(Long workId) {
        return obtainWorkByIdPort.execute(workId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Work> getWorksByUserId(Long userId) {
        return obtainWorkByIdPort.getWorksByUserId(userId);
    }
}
