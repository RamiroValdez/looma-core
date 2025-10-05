package com.amool.hexagonal.adapters.in.rest.dtos;

public record TranslationDto(
    String sourceLanguage,
    String targetLanguage,
    String originalText
) {}
