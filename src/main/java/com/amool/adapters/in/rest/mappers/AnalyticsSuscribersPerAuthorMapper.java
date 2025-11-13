package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.AnalyticsSuscribersPerAuthorDto;
import com.amool.domain.model.AnalyticsSuscribersPerAuthor;

public class AnalyticsSuscribersPerAuthorMapper {
    
    public static AnalyticsSuscribersPerAuthorDto toDto(AnalyticsSuscribersPerAuthor analyticsSuscribersPerWork){
        return new AnalyticsSuscribersPerAuthorDto(
            analyticsSuscribersPerWork.getUserId(),
            analyticsSuscribersPerWork.getAuthorId(),
            analyticsSuscribersPerWork.getSuscribedAt()
        );
    }

    public static AnalyticsSuscribersPerAuthor toDomain(AnalyticsSuscribersPerAuthorDto analyticsSuscribersPerWorkDto){
        return new AnalyticsSuscribersPerAuthor(
            analyticsSuscribersPerWorkDto.userId(),
            analyticsSuscribersPerWorkDto.authorId(),
            analyticsSuscribersPerWorkDto.suscribedAt()
        );
    }
}
