package com.ai.JobRecommendationSystem.service;

import com.ai.JobRecommendationSystem.dto.KafkaConnectorConfigDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class JRSConnectorService {

    //create kafka connector via rest template
//    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

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


@Autowired
    public JRSConnectorService(WebClient.Builder builder, @Value("${spring.kafka.kafkaconnectorurl}") String kafkaIp){
//        this.restTemplate = builder.build();
        this.webClient = builder.baseUrl(kafkaIp).build();
        this.objectMapper = new ObjectMapper();

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
            config.put("pk.mode", "record_key");
            config.put("pk.fields", "email");
            config.put("batch.size", "100");
            config.put("table.name.format","user_registration");
            config.put("offset.flush.timeout.ms", "5000");
//            config.put("key.converter", "org.apache.kafka.connect.storage.StringConverter");
//            config.put("value.converter", "io.confluent.connect.json.JsonSchemaConverter");
//            config.put("value.converter.schemas.enable", "true");
//            config.put("poll.interval.ms", "5000");

            connectorConfigDto.setConfig(config);
            try {
                webClient.delete()
                        .uri("/connectors/{name}", "postgres-sink-connector")
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        System.err.println("Failed to delete connector: " + body);
                                        return Mono.error(new RuntimeException("Delete failed: " + body));
                                    });
                        })
                        .bodyToMono(Void.class)
                        .block();
//
//                System.out.println("Connector deleted successfully");
            } catch (Exception e) {
                System.err.println("Failed to delete connector: " + e.getMessage());
            }

//            HttpEntity<KafkaConnectorConfigDto> request = new HttpEntity<>(connectorConfigDto,headers);
//            String url = kafkaip+"/connectors";

            try {
                //post http request
                webClient.post()
                        .uri("/connectors")
                        .header("Content-Type", "application/json")
                        .bodyValue(connectorConfigDto)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnNext(response -> System.out.println("Connector Created: " + response))
                        .doOnError(error -> System.err.println("Error creating connector: " + error.getMessage()))
                        .subscribe(
                                response -> System.out.println("Connector created successfully: " + response),
                                error -> System.err.println("Failed to create connector: " + error.getMessage()
                        ));
            } catch (HttpClientErrorException e) {
                System.err.println("Error creating connector: " + e.getResponseBodyAsString());
            }
        }
    }
