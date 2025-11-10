package com.amool.application.service;

import com.amool.application.port.out.SubscriptionPersistencePort;
import com.amool.application.port.out.PaymentProviderPort;
import com.amool.domain.model.PaymentInitResult;
import com.amool.domain.model.PaymentProviderType;
import com.amool.domain.model.SubscriptionType;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PaymentService {

    private final SubscriptionPersistencePort subscriptionPersistencePort;
    private final Map<PaymentProviderType, PaymentProviderPort> providers = new EnumMap<>(PaymentProviderType.class);

    public PaymentService(SubscriptionPersistencePort subscriptionPersistencePort,
                          List<PaymentProviderPort> providerAdapters) {
        this.subscriptionPersistencePort = subscriptionPersistencePort;
        for (PaymentProviderPort p : providerAdapters) {
            providers.put(p.supportedProvider(), p);
        }
    }

    @Transactional
    public void subscribe(Long userId, SubscriptionType type, Long targetId) {
        switch (type) {
            case CHAPTER -> subscriptionPersistencePort.subscribeChapter(userId, targetId);
            case AUTHOR -> subscriptionPersistencePort.subscribeAuthor(userId, targetId);
            case WORK -> subscriptionPersistencePort.subscribeWork(userId, targetId);
        }
    }

    @Transactional(readOnly = true)
    public PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId, PaymentProviderType provider) {
        return startCheckout(userId, type, targetId, provider, null);
    }

    @Transactional(readOnly = true)
    public PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId, PaymentProviderType provider, String returnUrl) {
        PaymentProviderPort adapter = providers.get(provider);
        if (adapter == null) {
            throw new IllegalArgumentException("Payment provider not configured: " + provider);
        }
        return adapter.startCheckout(userId, type, targetId, returnUrl);
    }
}
