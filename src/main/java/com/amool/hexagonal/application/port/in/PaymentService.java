package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.SubscriptionType;
import com.amool.hexagonal.domain.model.PaymentProviderType;
import com.amool.hexagonal.domain.model.PaymentInitResult;

public interface PaymentService {

    /**
     * Creates the subscription association. Can be extended to initiate a payment provider flow.
     */
    void subscribe(Long userId, SubscriptionType type, Long targetId);

    /**
     * Optionally start a checkout session with a payment provider and return initialization data
     * (e.g., redirect URL). Association into persistence should be completed by webhook.
     */
    PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId, PaymentProviderType provider);

    /**
     * Overload that allows passing a returnUrl to be used by the provider for redirects back to the client.
     */
    PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId, PaymentProviderType provider, String returnUrl);
}
