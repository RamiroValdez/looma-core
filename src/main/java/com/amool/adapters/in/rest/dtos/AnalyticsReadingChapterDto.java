package com.amool.adapters.in.rest.dtos;

import java.time.LocalDateTime;

public record AnalyticsReadingChapterDto(
    Long id,
    Long chapterId,
    LocalDateTime readAt,
    Long userId,
    Long workId
) {
}
