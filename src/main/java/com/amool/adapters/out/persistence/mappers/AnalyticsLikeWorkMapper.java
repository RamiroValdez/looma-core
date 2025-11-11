package com.amool.adapters.out.persistence.mappers;

import com.amool.adapters.out.persistence.entity.UserLikeEntity;
import com.amool.domain.model.AnalyticsLikeWork;

public class AnalyticsLikeWorkMapper {
    public static AnalyticsLikeWork toDomain(UserLikeEntity userLikeEntity) {
        return new AnalyticsLikeWork(
            userLikeEntity.getId(),
            userLikeEntity.getWork().getId(),
            userLikeEntity.getUser().getId(),
            userLikeEntity.getLikedAt()
        );
    }
}
