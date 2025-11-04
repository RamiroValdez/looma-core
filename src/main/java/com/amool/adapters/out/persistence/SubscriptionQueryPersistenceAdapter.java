package com.amool.adapters.out.persistence;

import com.amool.application.port.out.SubscriptionQueryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class SubscriptionQueryPersistenceAdapter implements SubscriptionQueryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean isSubscribedToAuthor(Long userId, Long authorId) {
        if (userId == null || authorId == null) return false;
        List<?> res = entityManager.createNativeQuery(
                "SELECT 1 FROM suscribe_autor WHERE user_id = :userId AND autor_id = :authorId LIMIT 1")
            .setParameter("userId", userId)
            .setParameter("authorId", authorId)
            .getResultList();
        return !res.isEmpty();
    }

    @Override
    public boolean isSubscribedToWork(Long userId, Long workId) {
        if (userId == null || workId == null) return false;
        List<?> res = entityManager.createNativeQuery(
                "SELECT 1 FROM suscribe_work WHERE user_id = :userId AND work_id = :workId LIMIT 1")
            .setParameter("userId", userId)
            .setParameter("workId", workId)
            .getResultList();
        return !res.isEmpty();
    }

    @Override
    public List<Long> unlockedChapters(Long userId, Long workId) {
        if (userId == null || workId == null) return List.of();
        List<?> rows = entityManager.createNativeQuery(
                "SELECT chapter_id FROM suscribe_chapter WHERE user_id = :userId AND work_id = :workId")
            .setParameter("userId", userId)
            .setParameter("workId", workId)
            .getResultList();
        List<Long> ids = new ArrayList<>();
        for (Object o : rows) {
            if (o == null) continue;
            if (o instanceof Number n) ids.add(n.longValue());
            else ids.add(Long.valueOf(o.toString()));
        }
        return ids;
    }
}
