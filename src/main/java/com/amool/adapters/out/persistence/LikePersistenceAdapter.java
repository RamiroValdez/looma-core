package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.UserEntity;
import com.amool.adapters.out.persistence.entity.WorkEntity;
import com.amool.adapters.out.persistence.entity.ChapterEntity;
import com.amool.adapters.out.persistence.entity.UserLikeEntity;
import com.amool.adapters.out.persistence.entity.ChapterLikeEntity;
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
    public Long likeWork(Long workId, Long userId) {
        WorkEntity work = entityManager.find(WorkEntity.class, workId);
        UserEntity user = entityManager.find(UserEntity.class, userId);
        
        if (work == null || user == null) {
            throw new EntityNotFoundException("Work or User not found");
        }

        if (hasUserLikedWork(workId, userId)) {
            return work.getLikes().longValue();
        }

        UserLikeEntity like = new UserLikeEntity(user, work);
        entityManager.persist(like);
        
        work.setLikes(work.getLikes() + 1);
        entityManager.merge(work);
        
        return work.getLikes().longValue();
    }

    @Override
    @Transactional
    public Long unlikeWork(Long workId, Long userId) {
        WorkEntity work = entityManager.find(WorkEntity.class, workId);
        
        if (work == null) {
            throw new EntityNotFoundException("Work not found with id: " + workId);
        }

        TypedQuery<UserLikeEntity> query = entityManager.createQuery(
            "SELECT l FROM UserLikeEntity l WHERE l.work.id = :workId AND l.user.id = :userId",
            UserLikeEntity.class
        );
        query.setParameter("workId", workId);
        query.setParameter("userId", userId);
        
        query.getResultStream().findFirst().ifPresent(like -> {
            entityManager.remove(like);
            work.setLikes(Math.max(0, work.getLikes() - 1));
            entityManager.merge(work);
        });
        
        return work.getLikes().longValue();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLikedWork(Long workId, Long userId) {
        TypedQuery<Boolean> query = entityManager.createQuery(
            "SELECT COUNT(l) > 0 FROM UserLikeEntity l WHERE l.work.id = :workId AND l.user.id = :userId",
            Boolean.class
        );
        query.setParameter("workId", workId);
        query.setParameter("userId", userId);
        
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public Long likeChapter(Long chapterId, Long userId) {
        ChapterEntity chapter = entityManager.find(ChapterEntity.class, chapterId);
        UserEntity user = entityManager.find(UserEntity.class, userId);
        
        if (chapter == null || user == null) {
            throw new EntityNotFoundException("Chapter or User not found");
        }

        if (hasUserLikedChapter(chapterId, userId)) {
            return chapter.getLikes();
        }

        ChapterLikeEntity like = new ChapterLikeEntity(user, chapter);
        entityManager.persist(like);
        
        chapter.setLikes(chapter.getLikes() + 1);
        entityManager.merge(chapter);
        
        return chapter.getLikes();
    }

    @Override
    @Transactional
    public Long unlikeChapter(Long chapterId, Long userId) {
        ChapterEntity chapter = entityManager.find(ChapterEntity.class, chapterId);
        
        if (chapter == null) {
            throw new EntityNotFoundException("Chapter not found with id: " + chapterId);
        }

        TypedQuery<ChapterLikeEntity> query = entityManager.createQuery(
            "SELECT l FROM ChapterLikeEntity l WHERE l.chapter.id = :chapterId AND l.user.id = :userId",
            ChapterLikeEntity.class
        );
        query.setParameter("chapterId", chapterId);
        query.setParameter("userId", userId);
        
        query.getResultStream().findFirst().ifPresent(like -> {
            entityManager.remove(like);
            chapter.setLikes(Math.max(0, chapter.getLikes() - 1));
            entityManager.merge(chapter);
        });
        
        return chapter.getLikes();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLikedChapter(Long chapterId, Long userId) {
        TypedQuery<Boolean> query = entityManager.createQuery(
            "SELECT COUNT(l) > 0 FROM ChapterLikeEntity l WHERE l.chapter.id = :chapterId AND l.user.id = :userId",
            Boolean.class
        );
        query.setParameter("chapterId", chapterId);
        query.setParameter("userId", userId);
        
        return query.getSingleResult();
    }
}
