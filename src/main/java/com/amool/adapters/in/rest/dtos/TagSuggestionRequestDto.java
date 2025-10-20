package com.amool.adapters.in.rest.dtos;

import java.util.Set;

public record TagSuggestionRequestDto(
        String description,
        String title,
        Set<String> existingTags
) {
}
