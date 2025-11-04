package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.application.port.out.UserQueryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class UserQueryPersistenceAdapter implements UserQueryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean existsById(Long userId) {
        if (userId == null) return false;
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM UserEntity u WHERE u.id = :id", Long.class)
                .setParameter("id", userId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public String findNameById(Long userId) {
        if (userId == null) return null;
        UserEntity u = entityManager.find(UserEntity.class, userId);
        if (u == null) return null;
        String name = u.getName();
        String surname = u.getSurname();
        return (name == null ? "" : name) + (surname == null ? "" : (name == null ? "" : " ") + surname);
    }
}
