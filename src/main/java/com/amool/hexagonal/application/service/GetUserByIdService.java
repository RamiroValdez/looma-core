package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.GetUserByIdUseCase;
import com.amool.hexagonal.application.port.out.LoadUserPort;
import com.amool.hexagonal.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GetUserByIdService implements GetUserByIdUseCase {

    private final LoadUserPort loadUserPort;

    public GetUserByIdService(LoadUserPort loadUserPort) {
        this.loadUserPort = loadUserPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getById(Long userId) {
        return loadUserPort.getById(userId);
    }
}
