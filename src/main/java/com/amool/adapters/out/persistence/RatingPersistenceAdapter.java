package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.RatingEntity;
import com.amool.application.port.out.RatingPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RatingPersistenceAdapter implements RatingPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public double rateWork(Long workId, Long userId, double rating, LocalDateTime createdAt) {
        TypedQuery<RatingEntity> query = entityManager.createQuery(
            "SELECT r FROM RatingEntity r WHERE r.workId = :workId AND r.userId = :userId", 
            RatingEntity.class
        );
        query.setParameter("workId", workId);
        query.setParameter("userId", userId);
        
        RatingEntity ratingEntity = query.getResultStream()
            .findFirst()
            .orElseGet(() -> {
                RatingEntity newRating = new RatingEntity(workId, userId, rating);
                newRating.setCreatedAt(createdAt);
                entityManager.persist(newRating);
                return newRating;
            });
        
        ratingEntity.setRating(rating);
        
        updateWorkAverageRating(workId);
        
        return rating;
    }

    @Override
    public Optional<Double> getUserRating(Long workId, Long userId) {
        TypedQuery<Double> query = entityManager.createQuery(
            "SELECT r.rating FROM RatingEntity r WHERE r.workId = :workId AND r.userId = :userId", 
            Double.class
        );
        query.setParameter("workId", workId);
        query.setParameter("userId", userId);
        
        return query.getResultStream().findFirst();
    }

    @Override
    public Double getAverageRating(Long workId) {
        return entityManager.createQuery(
            "SELECT COALESCE(AVG(r.rating), 0.0) FROM RatingEntity r WHERE r.workId = :workId", 
            Double.class
        )
        .setParameter("workId", workId)
        .getSingleResult();
    }

    @Override
    public Integer getTotalRatingsCount(Long workId) {
        Long total = entityManager.createQuery(
            "SELECT COUNT(r) FROM RatingEntity r WHERE r.workId = :workId", 
            Long.class
        )
        .setParameter("workId", workId)
        .getSingleResult();

        return total.intValue();
    }

    @Override
    public Page<RatingDto> getWorkRatings(Long workId, Pageable pageable) {
        TypedQuery<RatingEntity> query = entityManager.createQuery(
            "SELECT r FROM RatingEntity r WHERE r.workId = :workId", 
            RatingEntity.class
        );
        query.setParameter("workId", workId);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<RatingDto> ratings = query.getResultStream()
            .map(r -> new RatingDto(r.getUserId(), r.getRating()))
            .collect(Collectors.toList());

        Integer total = getTotalRatingsCount(workId);
        return new PageImpl<>(ratings, pageable, total);
    }

    @Transactional
    protected void updateWorkAverageRating(Long workId) {
        Double averageRating = getAverageRating(workId);
        Long ratingCount = entityManager.createQuery(
            "SELECT COUNT(r) FROM RatingEntity r WHERE r.workId = :workId", 
            Long.class
        )
        .setParameter("workId", workId)
        .getSingleResult();

        entityManager.createQuery(
            "UPDATE WorkEntity w SET w.averageRating = :avgRating, w.ratingCount = :count WHERE w.id = :workId"
        )
        .setParameter("avgRating", averageRating)
        .setParameter("count", ratingCount.intValue())
        .setParameter("workId", workId)
        .executeUpdate();
    }
}
