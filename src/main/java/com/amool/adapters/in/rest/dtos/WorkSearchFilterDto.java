package com.amool.adapters.in.rest.dtos;

import java.util.Set;

public record WorkSearchFilterDto(
        Set<Long> categoryIds,
        Set<Long> formatIds,
         String state,
         Integer minLikes,
         String text,
         String sortBy,
         Boolean asc
) {}
