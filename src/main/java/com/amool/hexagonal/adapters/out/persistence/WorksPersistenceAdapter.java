package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.WorkEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.WorkMapper;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Work;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class WorksPersistenceAdapter implements ObtainWorkByIdPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Work execute(Long workId) {
        String jpql = "SELECT DISTINCT w FROM WorkEntity w " +
                      "LEFT JOIN FETCH w.creator " +
                      "LEFT JOIN FETCH w.formatEntity " +
                      "LEFT JOIN FETCH w.chapters " +
                      "LEFT JOIN FETCH w.categories " +
                      "WHERE w.id = :workId";
        
        WorkEntity entity = entityManager.createQuery(jpql, WorkEntity.class)
                .setParameter("workId", workId)
                .getSingleResult();

        if (entity == null) {
            return null;
        }

        Work work = WorkMapper.toDomain(entity);

        return work;
    }
}

