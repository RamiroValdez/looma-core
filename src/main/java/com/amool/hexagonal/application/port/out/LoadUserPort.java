package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.User;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> getById(Long userId);
}
