package com.amool.hexagonal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {

        private final String ACCESS_KEY = System.getenv("AWS_ACCESS_KEY");

        private final String SECRET_KEY = System.getenv("AWS_SECRET");

        private final String REGION = System.getenv("AWS_REGION");

        @Bean
        public S3Client s3Client() {
            return S3Client.builder()
                    .region(Region.of(REGION))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)
                            )
                    )
                    .build();
        }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(REGION))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)
                        )
                )
                .build();
    }
}
