package com.amool.config;

import com.amool.application.port.out.*;
import com.amool.application.service.ImagesService;
import com.amool.application.service.PublishingSchedulerService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class ServicesConfig {

    private final FilesStoragePort filesStoragePort;
    private final ChatClient.Builder chatClientBuilder;
    private final SubscriptionPersistencePort subscriptionPersistencePort;
    private final List<PaymentProviderPort> paymentProviders;
    private final FindChaptersDueForPublicationPort findChaptersDueForPublicationPort;
    private final UpdateChapterStatusPort updateChapterStatusPort;
    private final HttpDownloadPort httpDownloadPort;

    public ServicesConfig(FilesStoragePort filesStoragePort,
                          ChatClient.Builder chatClientBuilder,
                          SubscriptionPersistencePort subscriptionPersistencePort,
                          List<PaymentProviderPort> paymentProviders,
                          FindChaptersDueForPublicationPort findChaptersDueForPublicationPort,
                          UpdateChapterStatusPort updateChapterStatusPort,
                          HttpDownloadPort httpDownloadPort) {
        this.filesStoragePort = filesStoragePort;
        this.chatClientBuilder = chatClientBuilder;
        this.subscriptionPersistencePort = subscriptionPersistencePort;
        this.paymentProviders = paymentProviders;
        this.findChaptersDueForPublicationPort = findChaptersDueForPublicationPort;
        this.updateChapterStatusPort = updateChapterStatusPort;
        this.httpDownloadPort = httpDownloadPort;
    }

    @Bean
    public ImagesService uploaderService() {
        return new ImagesService(filesStoragePort, httpDownloadPort);
    }

    @Bean
    public PublishingSchedulerService publishingSchedulerService() {
        return new PublishingSchedulerService(findChaptersDueForPublicationPort, updateChapterStatusPort);
    }
}
