package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.PaymentService;
import com.amool.hexagonal.application.port.out.SubscriptionPersistencePort;
import com.amool.hexagonal.application.port.out.payment.PaymentProviderPort;
import com.amool.hexagonal.domain.model.PaymentInitResult;
import com.amool.hexagonal.domain.model.PaymentProviderType;
import com.amool.hexagonal.domain.model.SubscriptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final SubscriptionPersistencePort subscriptionPersistencePort;
    private final Map<PaymentProviderType, PaymentProviderPort> providers = new EnumMap<>(PaymentProviderType.class);

    public PaymentServiceImpl(SubscriptionPersistencePort subscriptionPersistencePort,
                              List<PaymentProviderPort> providerAdapters) {
        this.subscriptionPersistencePort = subscriptionPersistencePort;
        for (PaymentProviderPort p : providerAdapters) {
            providers.put(p.supportedProvider(), p);
        }
    }

    @Override
    @Transactional
    public void subscribe(Long userId, SubscriptionType type, Long targetId) {
        switch (type) {
            case CHAPTER -> subscriptionPersistencePort.subscribeChapter(userId, targetId);
            case AUTHOR -> subscriptionPersistencePort.subscribeAuthor(userId, targetId);
            case WORK -> subscriptionPersistencePort.subscribeWork(userId, targetId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId, PaymentProviderType provider) {
        return startCheckout(userId, type, targetId, provider, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentInitResult startCheckout(Long userId, SubscriptionType type, Long targetId, PaymentProviderType provider, String returnUrl) {
        PaymentProviderPort adapter = providers.get(provider);
        if (adapter == null) {
            throw new IllegalArgumentException("Payment provider not configured: " + provider);
        }
        return adapter.startCheckout(userId, type, targetId, returnUrl);
    }
}
