package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.mappers.AnalyticsLikeWorkMapper;
import com.amool.adapters.out.persistence.mappers.AnalyticsLikeChapterMapper;
import com.amool.adapters.out.persistence.mappers.AnalyticsSavedWorkMapper;
import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.*;

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
    public List<AnalyticsRetention> getRetentionTotalsPerChapter(Long workId){
        
        String jpql = """
            SELECT rh.chapterId, COUNT(DISTINCT rh.userId)
            FROM ReadingHistoryEntity rh
            WHERE rh.workId = :workId
            GROUP BY rh.chapterId
            ORDER BY MIN(rh.readAt) ASC
        """;
        List<Object[]> rows = entityManager.createQuery(jpql, Object[].class)
                .setParameter("workId", workId)
                .getResultList();

        return rows.stream()
                .map(row -> {
                    AnalyticsRetention dto = new AnalyticsRetention();
                    dto.setChapter(((Number) row[0]).longValue());
                    dto.setTotalReaders(((Number) row[1]).longValue());
                    return dto;
                })
                .toList();
    }

    @Override
    public List<ReadingHistory> getReadingHistory(Long chapterId) {

        String jpql = "SELECT rh FROM ReadingHistoryEntity rh WHERE rh.chapterId = :chapterId";

        List<ReadingHistoryEntity> result = entityManager.createQuery(jpql, ReadingHistoryEntity.class)
                .setParameter("chapterId", chapterId)
                .getResultList();

        return result.stream().map(entity -> new ReadingHistory(
                entity.getId(),
                entity.getUserId(),
                entity.getWorkId(),
                entity.getChapterId(),
                entity.getReadAt()
        )).collect(Collectors.toList());
    }
}
