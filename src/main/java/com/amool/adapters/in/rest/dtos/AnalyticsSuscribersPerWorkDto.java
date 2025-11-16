package com.amool.adapters.in.rest.dtos;

import java.time.LocalDateTime;

public record AnalyticsSuscribersPerWorkDto(Long userId, Long authorId, LocalDateTime suscribedAt) {
    
}
