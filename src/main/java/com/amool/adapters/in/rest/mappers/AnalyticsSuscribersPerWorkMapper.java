package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.AnalyticsSuscribersPerWorkDto;
import com.amool.domain.model.AnalyticsSuscribersPerWork;

public class AnalyticsSuscribersPerWorkMapper {

    public static AnalyticsSuscribersPerWorkDto toDto(AnalyticsSuscribersPerWork analyticsSuscribersPerWork){
        return new AnalyticsSuscribersPerWorkDto(
            analyticsSuscribersPerWork.getUserId(), 
            analyticsSuscribersPerWork.getAuthorId(), 
            analyticsSuscribersPerWork.getSuscribedAt());
    }

    public static AnalyticsSuscribersPerWork toDomain(AnalyticsSuscribersPerWorkDto analyticsSuscribersPerWorkDto){
        return new AnalyticsSuscribersPerWork(
            analyticsSuscribersPerWorkDto.userId(), 
            analyticsSuscribersPerWorkDto.authorId(), 
            analyticsSuscribersPerWorkDto.suscribedAt());
    }
}
