package com.amool.application.port.out;

import com.amool.domain.model.PaymentInitResult;
import com.amool.domain.model.PaymentProviderType;
import com.amool.domain.model.SubscriptionType;

public interface PaymentProviderPort {
    PaymentProviderType supportedProvider();
    PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId);

    /**
     * Overload that allows passing a returnUrl for provider redirects. Default delegates to the 3-arg version.
     */
    default PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId, String returnUrl) {
        return startCheckout(userId, type, targetId);
    }
}
