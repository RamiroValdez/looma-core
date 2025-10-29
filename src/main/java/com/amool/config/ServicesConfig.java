package com.amool.config;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.service.ChatService;
import com.amool.application.service.ImagesService;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {

    private final AwsS3Port awsS3Port;
    private final ChatClient.Builder chatClientBuilder;

    public ServicesConfig(AwsS3Port awsS3Port, ChatClient.Builder chatClientBuilder) {
        this.awsS3Port = awsS3Port;
        this.chatClientBuilder = chatClientBuilder;
    }

    @Bean
    public ImagesService uploaderService() {
        return new ImagesService(awsS3Port);
    }

    @Bean
    public ChatService chatService() {
        return new ChatService(chatClientBuilder);
    }

}
