package com.amool.application.usecases;

import java.util.List;

import com.amool.application.port.out.SubscriptionPersistencePort;
import com.amool.domain.model.Work;

public class GetSubscriptions {

    private final SubscriptionPersistencePort subscriptionPersistencePort;

    public GetSubscriptions(SubscriptionPersistencePort subscriptionPersistencePort) {
        this.subscriptionPersistencePort = subscriptionPersistencePort;
    }

    public List<Work> execute(Long userId) {
        return subscriptionPersistencePort.getAllSubscribedWorks(userId);
    }
    
}
