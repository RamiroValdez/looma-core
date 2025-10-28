package com.amool.hexagonal.domain.model;

public class PaymentInitResult {
    private final PaymentProviderType provider;
    private final String redirectUrl;
    private final String externalReference;

    public PaymentInitResult(PaymentProviderType provider, String redirectUrl, String externalReference) {
        this.provider = provider;
        this.redirectUrl = redirectUrl;
        this.externalReference = externalReference;
    }

    public static PaymentInitResult of(PaymentProviderType provider, String redirectUrl, String externalReference) {
        return new PaymentInitResult(provider, redirectUrl, externalReference);
    }

    public PaymentProviderType getProvider() { return provider; }
    public String getRedirectUrl() { return redirectUrl; }
    public String getExternalReference() { return externalReference; }
}
