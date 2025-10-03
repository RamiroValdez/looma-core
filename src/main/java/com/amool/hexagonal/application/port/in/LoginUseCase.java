package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.User;

import java.util.Optional;

public interface LoginUseCase {
    Optional<User> login(String email, String password);
}
