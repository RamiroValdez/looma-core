package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.adapters.out.persistence.mappers.WorkMapper;
import com.amool.application.port.out.SaveWorkPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

@Component
@Transactional
public class SaveWorkPersistenceAdapter implements SaveWorkPort {

    private final UserPersistenceAdapter userPersistenceAdapter;
    private final WorksPersistenceAdapter worksPersistenceAdapter;
    private final EntityManager entityManager;

    public SaveWorkPersistenceAdapter(UserPersistenceAdapter userPersistenceAdapter, 
                                    WorksPersistenceAdapter worksPersistenceAdapter,
                                    EntityManager entityManager) {
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.worksPersistenceAdapter = worksPersistenceAdapter;
        this.entityManager = entityManager;
    }

    @Override
    public void saveWorkForUser(Long userId, Long workId) {
        User user = userPersistenceAdapter.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        Work work = worksPersistenceAdapter.obtainWorkById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));
        
        UserEntity userEntity = entityManager.find(UserEntity.class, userId);
        WorkEntity workEntity = entityManager.find(WorkEntity.class, workId);
        
        if (userEntity != null && workEntity != null) {
            userEntity.getSavedWorks().add(workEntity);
            entityManager.merge(userEntity);
        }
    }

    @Override
    public void removeSavedWorkForUser(Long userId, Long workId) {
        UserEntity userEntity = entityManager.find(UserEntity.class, userId);
        if (userEntity == null) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        
        WorkEntity workEntity = entityManager.find(WorkEntity.class, workId);
        if (workEntity == null) {
            throw new EntityNotFoundException("Work not found with id: " + workId);
        }
        
        userEntity.getSavedWorks().remove(workEntity);
        entityManager.merge(userEntity);
    }

    @Override
    public boolean isWorkSavedByUser(Long userId, Long workId) {
        String jpql = "SELECT COUNT(w) > 0 FROM UserEntity u JOIN u.savedWorks w WHERE u.id = :userId AND w.id = :workId";
        return entityManager.createQuery(jpql, Boolean.class)
                .setParameter("userId", userId)
                .setParameter("workId", workId)
                .getSingleResult();
    }
    
    @Override
    public List<Work> getSavedWorksByUser(Long userId) {
        String jpql = "SELECT DISTINCT w FROM UserEntity u JOIN u.savedWorks w " +
                     "LEFT JOIN FETCH w.creator " +
                     "LEFT JOIN FETCH w.formatEntity " +
                     "LEFT JOIN FETCH w.chapters " +
                     "WHERE u.id = :userId";
        
        List<WorkEntity> workEntities = entityManager.createQuery(jpql, WorkEntity.class)
                .setParameter("userId", userId)
                .getResultList();
                
        return workEntities.stream()
                .map(WorkMapper::toDomain)
                .collect(Collectors.toList());
    }
}
