package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.mappers.AnalyticsLikeWorkMapper;
import com.amool.adapters.out.persistence.mappers.AnalyticsLikeChapterMapper;
import com.amool.adapters.out.persistence.entity.UserLikeEntity;
import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.AnalyticsLikeChapter;
import com.amool.domain.model.AnalyticsLikeWork;
import java.util.List;
import java.util.stream.Collectors;
import com.amool.adapters.out.persistence.entity.ChapterLikeEntity;
import org.springframework.stereotype.Component;
import jakarta.persistence.EntityManager;

@Component
public class AnalyticsPersistenceAdapter implements AnalyticsPort {
    private EntityManager entityManager;
    
    public AnalyticsPersistenceAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    public List<AnalyticsLikeWork> getLikesPerWork(Long workId) {
    
        String jpql = "SELECT ul FROM UserLikeEntity ul WHERE ul.work.id = :workId";
        
       List<UserLikeEntity> result = entityManager.createQuery(jpql, UserLikeEntity.class)
                                    .setParameter("workId", workId)
                                    .getResultList();

        return result.stream().map(AnalyticsLikeWorkMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsLikeChapter> getLikesPerChapter(Long chapterId) {
        String jpql = "SELECT ul FROM ChapterLikeEntity ul WHERE ul.chapter.id = :chapterId";
        
       List<ChapterLikeEntity> result = entityManager.createQuery(jpql, ChapterLikeEntity.class)
                                    .setParameter("chapterId", chapterId)
                                    .getResultList();

        return result.stream().map(AnalyticsLikeChapterMapper::toDomain).collect(Collectors.toList());
    }
}
