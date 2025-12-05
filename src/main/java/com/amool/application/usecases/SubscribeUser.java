package com.amool.application.usecases;

import com.amool.application.port.out.SubscriptionPersistencePort;
import com.amool.domain.model.SubscriptionType;

public class SubscribeUser {

    private final SubscriptionPersistencePort subscriptionPersistencePort;

    public SubscribeUser(SubscriptionPersistencePort subscriptionPersistencePort) {
        this.subscriptionPersistencePort = subscriptionPersistencePort;
    }

    public void execute(Long userId, SubscriptionType type, Long targetId) {
        switch (type) {
            case CHAPTER -> subscriptionPersistencePort.subscribeChapter(userId, targetId);
            case AUTHOR -> subscriptionPersistencePort.subscribeAuthor(userId, targetId);
            case WORK -> subscriptionPersistencePort.subscribeWork(userId, targetId);
        }
    }
}
