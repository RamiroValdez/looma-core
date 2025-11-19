package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.mappers.AnalyticsLikeWorkMapper;
import com.amool.adapters.out.persistence.mappers.AnalyticsLikeChapterMapper;
import com.amool.adapters.out.persistence.mappers.AnalyticsSavedWorkMapper;
import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.AnalyticsLikeChapter;
import com.amool.domain.model.AnalyticsLikeWork;
import com.amool.domain.model.AnalyticsRatingWork;
import com.amool.domain.model.AnalyticsRetentionTotal;
import com.amool.domain.model.AnalyticsSuscribersPerAuthor;
import com.amool.domain.model.AnalyticsSuscribersPerWork;
import com.amool.domain.model.WorkSaved;
import java.util.List;
import java.util.stream.Collectors;

import com.amool.adapters.out.persistence.mappers.AnalyticsRatingWorkMapper;
import com.amool.adapters.out.persistence.mappers.AnalyticsSuscribersPerAuthorMapper;
import com.amool.adapters.out.persistence.mappers.AnalyticsSuscribersPerWorkMapper;
import com.amool.adapters.out.persistence.entity.*;
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

    @Override
    public List<AnalyticsRatingWork> getRatingsPerWork(Long workId) {
        String jpql = "SELECT ul FROM RatingEntity ul WHERE ul.workId = :workId";
        
       List<RatingEntity> result = entityManager.createQuery(jpql, RatingEntity.class)
                                    .setParameter("workId", workId)
                                    .getResultList();

        return result.stream().map(AnalyticsRatingWorkMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<WorkSaved> getSavedWorks(Long workId) {
        String jpql = "SELECT ul FROM WorkSavedEntity ul WHERE ul.work.id = :workId";
        
       List<WorkSavedEntity> result = entityManager.createQuery(jpql, WorkSavedEntity.class)
                                    .setParameter("workId", workId)
                                    .getResultList();

        return result.stream().map(AnalyticsSavedWorkMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Long getTotalPerAuthor(Long authorId) {
        String jpql = "SELECT COUNT(*) FROM SuscribeAutorEntity ul WHERE ul.autorId = :authorId";
        
       Long result = entityManager.createQuery(jpql, Long.class)
                                    .setParameter("authorId", authorId)
                                    .getSingleResult();

        return result;
    }

    @Override
    public Long getTotalPerWork(Long workId) {
        String jpql = "SELECT COUNT(*) FROM SuscribeWorkEntity ul WHERE ul.workId = :workId";
        
       Long result = entityManager.createQuery(jpql, Long.class)
                                    .setParameter("workId", workId)
                                    .getSingleResult();

        return result;
    }

    @Override
    public List<AnalyticsSuscribersPerWork> getSuscribersPerWork(Long workId) {
        String jpql = "SELECT ul FROM SuscribeWorkEntity ul WHERE ul.workId = :workId";
        
       List<SuscribeWorkEntity> result = entityManager.createQuery(jpql, SuscribeWorkEntity.class)
                                    .setParameter("workId", workId)
                                    .getResultList();

        return result.stream().map(AnalyticsSuscribersPerWorkMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsSuscribersPerAuthor> getSuscribersPerAuthor(Long authorId) {
        String jpql = "SELECT ul FROM SuscribeAutorEntity ul WHERE ul.autorId = :authorId";
        
       List<SuscribeAutorEntity> result = entityManager.createQuery(jpql, SuscribeAutorEntity.class)
                                    .setParameter("authorId", authorId)
                                    .getResultList();

        return result.stream().map(AnalyticsSuscribersPerAuthorMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsRetentionTotal> getRetentionTotalsPerChapter(Long workId){
        
        String sql = """
            SELECT 
                c.id AS chapterId,
                COUNT(DISTINCT urp.user_id) AS readers
            FROM user_reading_progress urp
            JOIN chapter c ON urp.chapter_id = c.id
            WHERE urp.work_id = :workId
            GROUP BY c.id, c.published_at
            ORDER BY c.published_at ASC
        """;

        List<Object[]> rows = entityManager.createNativeQuery(sql)
                            .setParameter("workId", workId)
                            .getResultList();

        List<AnalyticsRetentionTotal> result = rows.stream()
            .map(row -> {
                AnalyticsRetentionTotal dto = new AnalyticsRetentionTotal();
                dto.setChapter(((Number) row[0]).longValue());
                dto.setTotalReaders(((Number) row[1]).longValue());
                return dto;
            })
        .toList();

        return result;
    }
}
