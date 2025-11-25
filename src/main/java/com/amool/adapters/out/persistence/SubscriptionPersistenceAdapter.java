package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.adapters.out.persistence.mappers.WorkMapper;
import com.amool.application.port.out.SubscriptionPersistencePort;
import com.amool.domain.model.Work;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
@Transactional
public class SubscriptionPersistenceAdapter implements SubscriptionPersistencePort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void subscribeChapter(Long userId, Long chapterId) {
        entityManager.createNativeQuery(
                "INSERT INTO suscribe_chapter (user_id, work_id, chapter_id, created_at, notified) " +
                "SELECT :userId, c.work_id, c.id, CURRENT_TIMESTAMP, false FROM chapter c WHERE c.id = :chapterId " +
                "ON CONFLICT ON CONSTRAINT suscribe_chapter_pkey DO UPDATE SET created_at = CURRENT_TIMESTAMP")
                .setParameter("userId", userId)
                .setParameter("chapterId", chapterId)
                .executeUpdate();
    }

    @Override
    public void subscribeAuthor(Long userId, Long authorId) {
        entityManager.createNativeQuery(
                "INSERT INTO suscribe_autor (user_id, autor_id, subscribed_at, notified) " +
                "VALUES (:userId, :authorId, CURRENT_TIMESTAMP, false) " +
                "ON CONFLICT (user_id, autor_id) DO UPDATE SET created_at = CURRENT_TIMESTAMP")
                .setParameter("userId", userId)
                .setParameter("authorId", authorId)
                .executeUpdate();
    }

    @Override
    public void subscribeWork(Long userId, Long workId) {
        entityManager.createNativeQuery(
                "INSERT INTO suscribe_work (user_id, work_id, subscribed_at, notified) " +
                "VALUES (:userId, :workId, CURRENT_TIMESTAMP, false) " +
                "ON CONFLICT (user_id, work_id) DO UPDATE SET created_at = CURRENT_TIMESTAMP")
                .setParameter("userId", userId)
                .setParameter("workId", workId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public List<Work> getAllSubscribedWorks(Long userId) {
        String jpql = "SELECT w FROM WorkEntity w " +
                    "WHERE w.creator.id IN " +
                    "(SELECT sa.autorId FROM SuscribeAutorEntity sa WHERE sa.userId = :userId) " +
                    "OR w.id IN " +
                    "(SELECT sw.workId FROM SuscribeWorkEntity sw WHERE sw.userId = :userId)";
        
        List<WorkEntity> entities = entityManager
            .createQuery(jpql, WorkEntity.class)
            .setParameter("userId", userId)
            .getResultList();
        
        return entities.stream()
                .map(WorkMapper::toDomain)
                .toList();
    }

    
}
