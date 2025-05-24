package com.ai.JobRecommendationSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConnectorConfigDto {
    private String name;
    private Map<String,String> config;
}
