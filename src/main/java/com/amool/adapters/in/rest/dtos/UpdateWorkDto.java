package com.amool.adapters.in.rest.dtos;

import java.math.BigDecimal;
import java.util.Set;

public record UpdateWorkDto(
    BigDecimal price,
    String state,
    Set<String> tagIds
) {}
