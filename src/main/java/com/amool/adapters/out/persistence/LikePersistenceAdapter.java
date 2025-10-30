package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.adapters.out.persistence.entity.UserLikeEntity;
import com.amool.application.port.out.LikePort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LikePersistenceAdapter implements LikePort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public int likeWork(Long workId, Long userId) {
        WorkEntity work = entityManager.find(WorkEntity.class, workId);
        UserEntity user = entityManager.find(UserEntity.class, userId);
        
        if (work == null || user == null) {
            throw new EntityNotFoundException("Work or User not found");
        }

        if (hasUserLikedWork(workId, userId)) {
            return work.getLikes();
        }

        UserLikeEntity like = new UserLikeEntity(user, work);
        entityManager.persist(like);
        
        work.setLikes(work.getLikes() + 1);
        entityManager.merge(work);
        
        return work.getLikes();
    }

    @Override
    @Transactional
    public int unlikeWork(Long workId, Long userId) {
        WorkEntity work = entityManager.find(WorkEntity.class, workId);
        
        if (work == null) {
            throw new EntityNotFoundException("Work not found with id: " + workId);
        }

        TypedQuery<UserLikeEntity> query = entityManager.createQuery(
            "SELECT l FROM UserLike l WHERE l.work.id = :workId AND l.user.id = :userId", 
            UserLikeEntity.class
        );
        query.setParameter("workId", workId);
        query.setParameter("userId", userId);
        
        query.getResultStream().findFirst().ifPresent(like -> {
            entityManager.remove(like);
            work.setLikes(Math.max(0, work.getLikes() - 1));
            entityManager.merge(work);
        });
        
        return work.getLikes();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLikedWork(Long workId, Long userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(l) > 0 FROM UserLike l WHERE l.work.id = :workId AND l.user.id = :userId", 
            Long.class
        );
        query.setParameter("workId", workId);
        query.setParameter("userId", userId);
        
        return query.getSingleResult() > 0;
    }
}
