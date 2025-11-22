package com.amool.adapters.in.rest.dtos;

public record AnalyticsRetentionDto(
    Long chapter,
    Long totalReaders,
    Double percentFromFirst,
    Double percentFromPrevious
) {
}