package com.ai.JobRecommendationSystem.service;

import com.ai.JobRecommendationSystem.dto.UserResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service
public class JRSConsumerService {

    private final KafkaConsumer<String, String> consumer;
    ConsumerRecords<String, String> records;


    public JRSConsumerService(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                              @Value("${spring.kafka.consumer.group-id}") String groupId,
                              @Value("${spring.kafka.topic}") String topic) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new KafkaConsumer<>(props);
        this.topic = topic;
        consumer.subscribe(Collections.singleton(topic));
          }

          private final String topic;

    public List<UserResponseDto> consumeTopics() {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
        for (ConsumerRecord<String, String> record : records) {
            String resultJson = record.value();
            try{
                ObjectMapper objectMapper = new ObjectMapper();
                List<UserResponseDto> userResponseDto = objectMapper.readValue(resultJson, new TypeReference<List<UserResponseDto>>() {});
                return userResponseDto;
            }catch (Exception e) {
                System.err.println("Failed to parse Kafka record: " + e.getMessage());
            }
        }
        return null;
    }

    @PreDestroy
    public void close() {
        consumer.close();
    }
}
