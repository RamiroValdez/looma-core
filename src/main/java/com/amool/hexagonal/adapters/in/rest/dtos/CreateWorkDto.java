package com.amool.hexagonal.adapters.in.rest.dtos;

import java.util.List;
import java.util.Set;

public record CreateWorkDto(
        String title,
        String description,
        List<Long> categoryIds,
        Long formatId,
        Long originalLanguageId,
        Set<String> tagIds,
        String coverIaUrl
) {


}
