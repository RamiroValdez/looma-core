package com.amool.adapters.in.rest.dtos;

import jakarta.validation.constraints.NotBlank;

public record LinkPaymentSessionRequest(
        @NotBlank String externalReference,
        String provider
) {}
