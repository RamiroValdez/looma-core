package com.amool.adapters.out.persistence.mappers;

import com.amool.adapters.out.persistence.entity.SuscribeAutorEntity;
import com.amool.domain.model.AnalyticsSuscribersPerAuthor;

public class AnalyticsSuscribersPerAuthorMapper {
    
    public static AnalyticsSuscribersPerAuthor toDomain(SuscribeAutorEntity suscribersPerAuthorEntity){
        return new AnalyticsSuscribersPerAuthor(
            suscribersPerAuthorEntity.getUserId(),
            suscribersPerAuthorEntity.getAutorId(),
            suscribersPerAuthorEntity.getCreatedAt()
        );
    }
}
