package com.amool.adapters.in.rest.dtos;

import java.time.LocalDateTime;

public record AnalyticsLikeWorkDto(Long likeId, Long workId, Long userId, LocalDateTime likedAt){

}  

