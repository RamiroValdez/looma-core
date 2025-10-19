package com.amool.adapters.out.persistence;

import com.amool.application.port.out.LoadWorkOwnershipPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class WorkOwnershipPersistenceAdapter implements LoadWorkOwnershipPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean isOwner(Long workId, Long userId) {
        Long creatorId = entityManager.createQuery(
                "SELECT w.creator.id FROM WorkEntity w WHERE w.id = :id", Long.class)
            .setParameter("id", workId)
            .getResultList()
            .stream()
            .findFirst()
            .orElse(null);
        return creatorId != null && creatorId.equals(userId);
    }
}
