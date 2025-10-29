package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.application.port.out.LikePort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LikePersistenceAdapter implements LikePort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public int incrementLikes(Long workId) {
        WorkEntity workEntity = entityManager.find(WorkEntity.class, workId);
        if (workEntity == null) {
            throw new EntityNotFoundException("Work not found with id: " + workId);
        }
        
        workEntity.setLikes(workEntity.getLikes() + 1);
        entityManager.merge(workEntity);
        
        return workEntity.getLikes();
    }

    @Override
    @Transactional
    public int decrementLikes(Long workId) {
        WorkEntity workEntity = entityManager.find(WorkEntity.class, workId);
        if (workEntity == null) {
            throw new EntityNotFoundException("Work not found with id: " + workId);
        }
        
        int newLikes = Math.max(0, workEntity.getLikes() - 1);
        workEntity.setLikes(newLikes);
        entityManager.merge(workEntity);
        
        return newLikes;
    }
}
