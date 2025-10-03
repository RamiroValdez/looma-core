package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.UserService;
import com.amool.hexagonal.application.port.out.LoadUserPort;
import com.amool.hexagonal.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final LoadUserPort loadUserPort;

    public UserServiceImpl(LoadUserPort loadUserPort) {
        this.loadUserPort = loadUserPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getById(Long userId) {
        return loadUserPort.getById(userId);
    }
}
