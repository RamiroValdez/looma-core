package com.amool.adapters.out.persistence;

import org.springframework.stereotype.Component;

import com.amool.application.port.out.ReadingProgressPort;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class ReadingProgressPersistenceAdapter implements ReadingProgressPort {
    
    private final EntityManager entityManager;

    public ReadingProgressPersistenceAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    public boolean update(Long userId, Long workId, Long chapterId) {
        boolean exists = checkExists(userId, workId);
        if (!exists) {
            return false;
        }
        
        String updateQuery = """
            UPDATE user_reading_progress 
            SET chapter_id = :chapterId
            WHERE user_id = :userId AND work_id = :workId
            """;
            
        entityManager.createNativeQuery(updateQuery)
            .setParameter("chapterId", chapterId)
            .setParameter("userId", userId)
            .setParameter("workId", workId)
            .executeUpdate();
            
        return true;
}
        
    @Override
    public boolean create(Long userId, Long workId, Long chapterId) {
        try {
        String insertQuery = """
        INSERT INTO user_reading_progress (user_id, work_id, chapter_id) 
        VALUES (:userId, :workId, :chapterId)
        """;
        
        entityManager.createNativeQuery(insertQuery)
        .setParameter("userId", userId)
        .setParameter("workId", workId)
        .setParameter("chapterId", chapterId)
        .executeUpdate();
        return true;
    } catch (Exception e) {
        return false;
    }
    }

    private boolean checkExists(Long userId, Long workId) {
        String checkQuery = """
            SELECT COUNT(*) > 0 
            FROM user_reading_progress 
            WHERE user_id = :userId AND work_id = :workId
            """;

        boolean exists = (boolean) entityManager.createNativeQuery(checkQuery)
            .setParameter("userId", userId)
            .setParameter("workId", workId)
            .getSingleResult();
            
        return exists;
    }
    
}
