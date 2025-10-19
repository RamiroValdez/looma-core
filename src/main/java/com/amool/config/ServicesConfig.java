package com.amool.config;

import com.amool.application.port.out.AwsS3Port;
import com.amool.application.service.ImagesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {

    private final AwsS3Port awsS3Port;

    public ServicesConfig(AwsS3Port awsS3Port) {
        this.awsS3Port = awsS3Port;
    }

    @Bean
    public ImagesService uploaderService() {
        return new ImagesService(awsS3Port);
    }

}
