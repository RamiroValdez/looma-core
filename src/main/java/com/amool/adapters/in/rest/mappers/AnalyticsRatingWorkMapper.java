package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.AnalyticsRatingWorkDto;
import com.amool.domain.model.AnalyticsRatingWork;

public class AnalyticsRatingWorkMapper {
    
    public static AnalyticsRatingWorkDto toDto(AnalyticsRatingWork analyticsRatingWork){
        return new AnalyticsRatingWorkDto(
            analyticsRatingWork.getRatingId(),
            analyticsRatingWork.getRating(),
            analyticsRatingWork.getUserId(),
            analyticsRatingWork.getWorkId(),
            analyticsRatingWork.getRatedAt()
        );
    }

    public static AnalyticsRatingWork toDomain(AnalyticsRatingWorkDto analyticsRatingWorkDto){
        return new AnalyticsRatingWork(
            analyticsRatingWorkDto.ratingId(),
            analyticsRatingWorkDto.rating(),
            analyticsRatingWorkDto.userId(),
            analyticsRatingWorkDto.workId(),
            analyticsRatingWorkDto.ratedAt()
        );
    }
}
