package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.adapters.out.persistence.entity.WorkSavedEntity;
import com.amool.adapters.out.persistence.mappers.WorkMapper;
import com.amool.application.port.out.SaveWorkPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

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
        WorkSavedEntity workSavedEntity = new WorkSavedEntity();
        UserEntity userEntity = entityManager.find(UserEntity.class, userId);
        WorkEntity workEntity = entityManager.find(WorkEntity.class, workId);
        workSavedEntity.setUser(userEntity);
        workSavedEntity.setWork(workEntity);
        workSavedEntity.setSavedAt(LocalDateTime.now());
        entityManager.persist(workSavedEntity);
    }

    @Override
    public void removeSavedWorkForUser(Long userId, Long workId) {
        if (!isWorkSavedByUser(userId, workId)) {
            throw new EntityNotFoundException("Work not saved by user");
        }
        WorkSavedEntity workSavedEntity = entityManager
                .createQuery("SELECT ws FROM WorkSavedEntity ws WHERE ws.work.id = :workId AND ws.user.id = :userId", 
                WorkSavedEntity.class)
                .setParameter("userId", userId)
                .setParameter("workId", workId)
                .getSingleResult();
                
        entityManager.remove(workSavedEntity);
    } 
    @Override
    public boolean isWorkSavedByUser(Long userId, Long workId) {
        String jpql = "SELECT COUNT(ws) > 0 FROM WorkSavedEntity ws WHERE ws.work.id = :workId AND ws.user.id = :userId";
        return entityManager.createQuery(jpql, Boolean.class)
                .setParameter("userId", userId)
                .setParameter("workId", workId)
                .getSingleResult();
    }
    
    @Override
    public List<Work> getSavedWorksByUser(Long userId) {
        String jpql = "SELECT ws.work FROM WorkSavedEntity ws " +
                    "WHERE ws.user.id = :userId";
        List<WorkEntity> workEntities = entityManager.createQuery(jpql, WorkEntity.class)
                .setParameter("userId", userId)
                .getResultList();

        return workEntities.stream()
                .map(WorkMapper::toDomain)
                .collect(Collectors.toList());
    }

}
