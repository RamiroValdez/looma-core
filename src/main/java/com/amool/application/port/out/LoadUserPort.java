package com.amool.application.port.out;

import com.amool.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface LoadUserPort {
    Optional<User> getById(Long userId);

    List<Long> getAllAuthorSubscribers(Long authorId);

    List<Long> getAllWorkSubscribers(Long workId);

    boolean updateUser(User user, String newPassword);
}
