package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.WorkEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.WorkMapper;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.application.port.out.WorkPort;
import com.amool.hexagonal.domain.model.Work;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class WorksPersistenceAdapter implements ObtainWorkByIdPort, WorkPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Work> obtainWorkById(Long workId) {
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


    @Override
    public Long createWork(Work work) {
        WorkEntity workEntity = WorkMapper.toEntity(work);
        entityManager.persist(workEntity);
        entityManager.flush();
        return workEntity.getId();
    }

    @Override
    public Boolean updateWork(Work work) {
        try {
            WorkEntity existingEntity = entityManager.find(WorkEntity.class, work.getId());

            if (existingEntity == null) {
                return false;
            }

            WorkEntity updatedEntity = WorkMapper.toEntity(work);
            updatedEntity.setId(existingEntity.getId());

            entityManager.merge(updatedEntity);
            entityManager.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean deleteWork(Long workId) {
        try {
            WorkEntity workEntity = entityManager.find(WorkEntity.class, workId);
            if (workEntity == null) {
                return false;
            }
            
            workEntity.getChapters().clear();
            workEntity.getCategories().clear();
            entityManager.flush();
            
            entityManager.remove(workEntity);
            entityManager.flush();
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
