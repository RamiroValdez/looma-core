package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.mappers.UserMapper;
import com.amool.application.port.out.AuthenticateUserPort;
import com.amool.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Component
public class UserAuthenticationPersistenceAdapter implements AuthenticateUserPort {

    private final EntityManager entityManager;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserAuthenticationPersistenceAdapter(EntityManager entityManager, PasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> authenticate(String email, String plainPassword) {
        TypedQuery<UserEntity> q = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.email = :email AND u.enabled = true", UserEntity.class);
        q.setParameter("email", email);
        return q.getResultList().stream()
                .filter(e -> {
                    String stored = e.getPassword();
                    if (stored == null) return false;
                    boolean isBcrypt = stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$");
                    return isBcrypt ? encoder.matches(plainPassword, stored) : stored.equals(plainPassword);
                })
                .findFirst()
                .map(UserMapper::toDomain);
    }
}

