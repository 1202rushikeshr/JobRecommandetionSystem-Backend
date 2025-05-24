package com.ai.JobRecommendationSystem.service;

import com.ai.JobRecommendationSystem.dto.KafkaConnectorConfigDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaConnectorService {

    //create kafka connector via rest template
    private final RestTemplate restTemplate;

    @Value("${spring.kafka.kafkaconnectorurl}")
    private String kafkaip;

    @Value("${spring.datasource.url}")
    private String connectionURL;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.jdbcconnectorclass}")
    private String jdbcConnector;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;



    public KafkaConnectorService(RestTemplateBuilder builder){
        this.restTemplate = builder.build();
    }

        public void createPostgresSinkConnector() {
            KafkaConnectorConfigDto connectorConfigDto = new KafkaConnectorConfigDto();
            connectorConfigDto.setName("postgres-sink-connector");
            Map<String, String> config = new HashMap<>();
            config.put("connector.class", jdbcConnector);
            config.put("tasks.max", "1");
            config.put("topics", topic);
            config.put("connection.url", connectionURL);
            config.put("connection.user", dbUsername);
            config.put("connection.password", dbPassword);
            config.put("insert.mode", "upsert");
            config.put("auto.create", "true");
            config.put("auto.evolve", "true");
            config.put("pk.mode", "record_value");
            config.put("pk.fields", "email");
            config.put("batch.size", "100");
            config.put("table.name.format","user_registration");
            config.put("offset.flush.timeout.ms", "5000");
            config.put("key.converter", "org.apache.kafka.connect.storage.StringConverter");
            config.put("value.converter", "org.apache.kafka.connect.json.JsonConverter");
            config.put("value.converter.schemas.enable", "false");
            config.put("poll.interval.ms", "5000");

            connectorConfigDto.setConfig(config);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<KafkaConnectorConfigDto> request = new HttpEntity<>(connectorConfigDto,headers);
            String url = kafkaip+"/connectors";

            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                System.out.println("Kafka Connect Response: " + response.getStatusCode());
                System.out.println(response.getBody());
            } catch (HttpClientErrorException e) {
                System.err.println("Error creating connector: " + e.getResponseBodyAsString());
            }
        }
    }
