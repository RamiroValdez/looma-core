package com.amool.application.usecases;

import java.util.List;

import com.amool.application.port.out.FilesStoragePort;
import com.amool.application.port.out.SubscriptionPersistencePort;
import com.amool.domain.model.Work;

public class GetSubscriptions {

    private final SubscriptionPersistencePort subscriptionPersistencePort;
    private final FilesStoragePort filesStoragePort;

    public GetSubscriptions(SubscriptionPersistencePort subscriptionPersistencePort, FilesStoragePort filesStoragePort) {
        this.subscriptionPersistencePort = subscriptionPersistencePort;
        this.filesStoragePort = filesStoragePort;
    }

    public List<Work> execute(Long userId) {
        List<Work> suscribedWorks = subscriptionPersistencePort.getAllSubscribedWorks(userId);

        suscribedWorks.forEach(work -> {
            String coverImageUrl = filesStoragePort.obtainPublicUrl(work.getCover());
            work.setCover(coverImageUrl);
        });

        return suscribedWorks;
    }
    
}
