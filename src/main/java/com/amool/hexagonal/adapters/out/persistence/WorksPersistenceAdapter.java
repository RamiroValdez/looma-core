package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.WorkEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.WorkMapper;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Work;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class WorksPersistenceAdapter implements ObtainWorkByIdPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Work> execute(Long workId) {
        String jpql = "SELECT DISTINCT w FROM WorkEntity w " +
                      "LEFT JOIN FETCH w.creator " +
                      "LEFT JOIN FETCH w.formatEntity " +
                      "LEFT JOIN FETCH w.chapters " +
                      "LEFT JOIN FETCH w.categories " +
                      "WHERE w.id = :workId";
        List<WorkEntity> results = entityManager.createQuery(jpql, WorkEntity.class)
                .setParameter("workId", workId)
                .setMaxResults(1)
                .getResultList();

        return results.stream()
                .findFirst()
                .map(WorkMapper::toDomain);
    }

    @Override
    public List<Work> getWorksByUserId(Long userId) {
        String jpql = "SELECT DISTINCT w FROM WorkEntity w " +
                      "LEFT JOIN FETCH w.creator c " +
                      "LEFT JOIN FETCH w.formatEntity " +
                      "LEFT JOIN FETCH w.chapters " +
                      "LEFT JOIN FETCH w.categories " +
                      "WHERE c.id = :userId";

        List<WorkEntity> entities = entityManager.createQuery(jpql, WorkEntity.class)
                .setParameter("userId", userId)
                .getResultList();

        return entities.stream()
                .map(WorkMapper::toDomain)
                .collect(Collectors.toList());
    }
}
