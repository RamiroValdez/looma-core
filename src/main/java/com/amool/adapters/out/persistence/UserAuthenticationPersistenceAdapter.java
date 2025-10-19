package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.mappers.UserMapper;
import com.amool.application.port.out.AuthenticateUserPort;
import com.amool.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuthenticationPersistenceAdapter implements AuthenticateUserPort {

    private final EntityManager entityManager;

    public UserAuthenticationPersistenceAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<User> authenticate(String email, String plainPassword) {
        TypedQuery<UserEntity> q = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class);
        q.setParameter("email", email);
        return q.getResultList().stream()
                .filter(e -> e.getPassword() != null && e.getPassword().equals(plainPassword))
                .findFirst()
                .map(UserMapper::toDomain);
    }
}

