package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.mappers.AnalyticsLikeWorkMapper;
import com.amool.adapters.out.persistence.entity.UserLikeEntity;
import com.amool.application.port.out.AnalyticsPort;
import com.amool.domain.model.AnalyticsLikeWork;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

public class AnalyticsPersistenceAdapter implements AnalyticsPort {
    private EntityManager entityManager;
    
    public AnalyticsPersistenceAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    public List<AnalyticsLikeWork> getLikesPerWork(Long workId) {
    
        String jpql = "SELECT ul FROM UserLikeEntity ul WHERE ul.workId = :workId";
        
       List<UserLikeEntity> result = entityManager.createQuery(jpql, UserLikeEntity.class)
                                    .setParameter("workId", workId)
                                    .getResultList();

        return result.stream().map(AnalyticsLikeWorkMapper::toDomain).collect(Collectors.toList());
    }
}
