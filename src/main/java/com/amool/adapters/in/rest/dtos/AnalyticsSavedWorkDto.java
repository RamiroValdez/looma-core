package com.amool.adapters.in.rest.dtos;

import java.time.LocalDateTime;

public record AnalyticsSavedWorkDto(Long savedId, Long userId, Long workId, LocalDateTime savedAt) {
    
}
