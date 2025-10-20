package com.amool.application.usecases;

import com.amool.application.port.out.AuthenticateUserPort;
import com.amool.domain.model.User;

import java.util.Optional;

public class LoginUseCase {

    private final AuthenticateUserPort authPort;

    public LoginUseCase(AuthenticateUserPort authPort) {
        this.authPort = authPort;
    }

    public Optional<User> execute(String email, String password) {
        return authPort.authenticate(email, password);
    }

}
