package com.amool.hexagonal.adapters.in.rest.dtos;

import com.amool.hexagonal.domain.model.PaymentInitResult;

public record PaymentInitResponse(
        String provider,
        String redirectUrl,
        String externalReference
) {
    public static PaymentInitResponse from(PaymentInitResult result) {
        return new PaymentInitResponse(
                result.getProvider().name().toLowerCase(),
                result.getRedirectUrl(),
                result.getExternalReference()
        );
    }
}
