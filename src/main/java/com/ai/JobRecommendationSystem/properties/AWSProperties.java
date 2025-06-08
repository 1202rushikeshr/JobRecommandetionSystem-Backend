package com.ai.JobRecommendationSystem.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AWSProperties {
    private String accessKey;
    private String secretKey;
    private String region;
}