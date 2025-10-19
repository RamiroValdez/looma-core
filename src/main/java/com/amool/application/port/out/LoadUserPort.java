package com.amool.application.port.out;

import com.amool.domain.model.User;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> getById(Long userId);
}
