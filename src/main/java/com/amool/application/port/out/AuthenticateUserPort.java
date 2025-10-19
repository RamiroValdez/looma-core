package com.amool.application.port.out;

import com.amool.domain.model.User;

import java.util.Optional;

public interface AuthenticateUserPort {
    Optional<User> authenticate(String email, String plainPassword);
}
