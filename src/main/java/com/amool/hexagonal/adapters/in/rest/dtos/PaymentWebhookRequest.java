package com.amool.hexagonal.adapters.in.rest.dtos;

import jakarta.validation.constraints.NotBlank;

public record PaymentWebhookRequest(
        @NotBlank String externalReference
) {}
