package com.amool.adapters.in.rest.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record CreateWorkDto(
        String title,
        String description,
        List<Long> categoryIds,
        Long formatId,
        Long originalLanguageId,
        BigDecimal price,
        Set<String> tagIds,
        String coverIaUrl
) {


}
