package com.ai.JobRecommendationSystem.config;

import com.ai.JobRecommendationSystem.properties.AWSProperties;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class TextractConfig {

    private final AWSProperties aws;

    @Autowired
    public TextractConfig(AWSProperties aws) {
        this.aws = aws;
    }


    @Bean
    public AmazonTextract amazonTextract() {
        BasicAWSCredentials creds = new BasicAWSCredentials(aws.getAccessKey(), aws.getSecretKey());

        return AmazonTextractClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withRegion(aws.getRegion())
                .build();
    }
}
