package com.amool.hexagonal.adapters.in.rest.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubscribeRequest(
        @NotBlank String subscriptionType,
        @NotNull Long targetId,
        Long workId,
        String provider,
        String returnUrl
) {}
