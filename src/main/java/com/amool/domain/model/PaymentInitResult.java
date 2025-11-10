package com.amool.domain.model;

public class PaymentInitResult {

    private final PaymentProviderType provider;
    private final String redirectUrl;
    private final String preferenceId;

    private PaymentInitResult(PaymentProviderType provider, String redirectUrl, String preferenceId) {
        this.provider = provider;
        this.redirectUrl = redirectUrl;
        this.preferenceId = preferenceId;
    }

    public static PaymentInitResult of(PaymentProviderType provider, String redirectUrl, String preferenceId) {
        return new PaymentInitResult(provider, redirectUrl, preferenceId);
    }

    public PaymentProviderType getProvider() {
        return provider;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public String getCheckoutUrl() {
        return redirectUrl;
    }

    public String getExternalId() {
        return preferenceId;
    }

    public String getExternalReference() {
        return preferenceId;
    }
}
