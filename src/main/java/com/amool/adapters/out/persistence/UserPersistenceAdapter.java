package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.mappers.UserMapper;
import com.amool.application.port.out.LoadUserPort;
import com.amool.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Component;

import java.util.List;
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

    @Override
    @Transactional
    public List<Long> getAllAuthorSubscribers(Long authorId) {
        String jpql = "SELECT sa.user.id FROM SuscribeAutorEntity sa " +
                    "WHERE sa.autor.id = :authorId";
        
        return entityManager.createQuery(jpql, Long.class)
            .setParameter("authorId", authorId)
            .getResultList();
    }

    @Override
    public List<Long> getAllWorkSubscribers(Long workId) {
        String jpql = "SELECT sw.user.id FROM SuscribeWorkEntity sw " +
                    "WHERE sw.work.id = :workId";
        
        return entityManager.createQuery(jpql, Long.class)
            .setParameter("workId", workId)
            .getResultList();
    }
}
