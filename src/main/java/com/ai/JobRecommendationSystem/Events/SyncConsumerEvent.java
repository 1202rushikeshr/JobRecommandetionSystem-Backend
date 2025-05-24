package com.ai.JobRecommendationSystem.Events;

import com.ai.JobRecommendationSystem.dto.RegistrationResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class SyncConsumerEvent {
    public String topic;
    ConsumerRecords<String, String> records;
    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    public RegistrationResponseDto consumeTopics(@Value("${spring.kafka.bootstrap-servers}")String bootstrapServers, @Value("${spring.kafka.topic}")String topic) {
        this.topic = topic;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        RegistrationResponseDto registrationResponseDto = null;
        try {
            while (true) {
                records = consumer.poll(Duration.ofMillis(3000));
                for (ConsumerRecord<String, String> record : records) {
                    registrationResponseDto = objectMapper.readValue(record.value(), RegistrationResponseDto.class);

                }
            }
        } catch (JsonProcessingException e) {
            e.getMessage();
        } finally {
            consumer.close();
        }
        return registrationResponseDto;
    }
}
