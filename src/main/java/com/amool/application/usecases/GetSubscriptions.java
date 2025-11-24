package com.amool.application.usecases;

import java.util.List;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.port.out.SubscriptionPersistencePort;
import com.amool.domain.model.Work;

public class GetSubscriptions {

    private final SubscriptionPersistencePort subscriptionPersistencePort;
    private final AwsS3Port awsS3Port;

    public GetSubscriptions(SubscriptionPersistencePort subscriptionPersistencePort, AwsS3Port awsS3Port) {
        this.subscriptionPersistencePort = subscriptionPersistencePort;
        this.awsS3Port = awsS3Port;
    }

    public List<Work> execute(Long userId) {
        List<Work> suscribedWorks = subscriptionPersistencePort.getAllSubscribedWorks(userId);

        suscribedWorks.forEach(work -> {
            String coverImageUrl = awsS3Port.obtainPublicUrl(work.getCover());
            work.setCover(coverImageUrl);
        });

        return suscribedWorks;
    }
    
}
