package com.amool.adapters.in.rest.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateChapterContentRequest(
    @NotBlank String workId,
    @NotBlank String chapterId,
    @NotBlank String language,
    @NotBlank String content
) {}
