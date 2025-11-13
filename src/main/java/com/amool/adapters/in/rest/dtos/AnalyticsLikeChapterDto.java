package com.amool.adapters.in.rest.dtos;

import java.time.LocalDateTime;

public record AnalyticsLikeChapterDto(Long likeId, Long chapterId, Long userId, LocalDateTime likedAt) {
    
}
