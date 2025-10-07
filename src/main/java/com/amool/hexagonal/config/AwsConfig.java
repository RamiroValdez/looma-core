package com.amool.hexagonal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {

        private String ACCESS_KEY;

        private String SECRET_KEY;

        private String REGION;

        public AwsConfig(
                @Value("${AWS_ACCESS_KEY}") String accessKey,
                @Value("${AWS_SECRET}") String secretKey,
                @Value("${AWS_REGION}") String region) {
            this.ACCESS_KEY = accessKey;
            this.SECRET_KEY = secretKey;
            this.REGION = region;
        }

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
