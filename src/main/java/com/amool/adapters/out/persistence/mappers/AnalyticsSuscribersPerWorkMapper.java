package com.amool.adapters.out.persistence.mappers;

import com.amool.adapters.out.persistence.entity.SuscribeWorkEntity;
import com.amool.domain.model.AnalyticsSuscribersPerWork;

public class AnalyticsSuscribersPerWorkMapper {
    public static AnalyticsSuscribersPerWork toDomain(SuscribeWorkEntity suscribeWorkEntity){
        return new AnalyticsSuscribersPerWork(
            suscribeWorkEntity.getUserId(), 
            suscribeWorkEntity.getWorkId(), 
            suscribeWorkEntity.getCreatedAt());
    }
}
