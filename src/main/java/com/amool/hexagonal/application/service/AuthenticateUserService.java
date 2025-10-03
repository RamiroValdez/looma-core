package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.LoginUseCase;
import com.amool.hexagonal.application.port.out.AuthenticateUserPort;
import com.amool.hexagonal.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticateUserService implements LoginUseCase {

    private final AuthenticateUserPort authPort;

    public AuthenticateUserService(AuthenticateUserPort authPort) {
        this.authPort = authPort;
    }

    @Override
    public Optional<User> login(String email, String password) {
        return authPort.authenticate(email, password);
    }
}
