package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.User;

import java.util.Optional;

public interface GetUserByIdUseCase {
    Optional<User> getById(Long userId);
}
