package com.amool.hexagonal.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateChapterContentRequest(
    @NotBlank String workId,
    @NotBlank String chapterId,
    @NotBlank String language,
    @NotBlank String content
) {}
