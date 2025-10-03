package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.CredentialsService;
import com.amool.hexagonal.application.port.out.AuthenticateUserPort;
import com.amool.hexagonal.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CredentialsServiceImpl implements CredentialsService {

    private final AuthenticateUserPort authPort;

    public CredentialsServiceImpl(AuthenticateUserPort authPort) {
        this.authPort = authPort;
    }

    @Override
    public Optional<User> login(String email, String password) {
        return authPort.authenticate(email, password);
    }
}
