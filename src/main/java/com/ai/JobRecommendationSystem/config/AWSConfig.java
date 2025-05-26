package com.ai.JobRecommendationSystem.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
    @Value("${aws.access.key}")
    String accessKey;
    @Value("${aws.secret.key}")
    String secretKey;
    @Bean
    public AmazonTextract amazonTextract() {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonTextractClientBuilder.standard()
                .withRegion(Regions.US_EAST_1) // Change to your region
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}
