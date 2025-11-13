package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.AnalyticsLikeWorkDto;
import com.amool.domain.model.AnalyticsLikeWork;

public class AnalyticsLikeWorkMapper {
    public static AnalyticsLikeWorkDto toDto(AnalyticsLikeWork analyticsLikeWork) {
        return new AnalyticsLikeWorkDto(
            analyticsLikeWork.getLikeId(),
            analyticsLikeWork.getWorkId(),
            analyticsLikeWork.getUserId(),
            analyticsLikeWork.getLikedAt()
        );
    }

    public static AnalyticsLikeWork toDomain(AnalyticsLikeWorkDto analyticsLikeWorkDto) {
        return new AnalyticsLikeWork(
            analyticsLikeWorkDto.likeId(),
            analyticsLikeWorkDto.workId(),
            analyticsLikeWorkDto.userId(),
            analyticsLikeWorkDto.likedAt()
        );
    }
}
