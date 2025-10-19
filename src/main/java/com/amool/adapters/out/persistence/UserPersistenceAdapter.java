package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.mappers.UserMapper;
import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements LoadUserPort {

    private final EntityManager entityManager;

    public UserPersistenceAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<User> getById(Long userId) {
        UserEntity entity = entityManager.find(UserEntity.class, userId);
        return Optional.ofNullable(UserMapper.toDomain(entity));
    }
}
