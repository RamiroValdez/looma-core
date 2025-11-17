package com.amool.adapters.out.persistence.mappers;

import com.amool.adapters.out.persistence.entity.RatingEntity;
import com.amool.domain.model.AnalyticsRatingWork;

public class AnalyticsRatingWorkMapper {

    public static AnalyticsRatingWork toDomain(RatingEntity ratingEntity){
        return new AnalyticsRatingWork(
            ratingEntity.getId(),
            ratingEntity.getRating(),
            ratingEntity.getUserId(),
            ratingEntity.getWorkId(),
            ratingEntity.getCreatedAt()
        );
    }
    
}
