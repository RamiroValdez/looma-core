package com.amool.application.usecases;

import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;

import java.util.Optional;

public class GetUserByIdUseCase {

    private final LoadUserPort loadUserPort;

    public GetUserByIdUseCase(LoadUserPort loadUserPort) {
        this.loadUserPort = loadUserPort;
    }

    public Optional<User> execute(Long userId) {
        return loadUserPort.getById(userId);
    }
}
