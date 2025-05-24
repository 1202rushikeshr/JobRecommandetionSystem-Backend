package com.ai.JobRecommendationSystem.Events;


import com.ai.JobRecommendationSystem.entity.JRS_Registration;
import com.ai.JobRecommendationSystem.repository.JRS_RegistrationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Component
public class SyncProducerEvent {

    private KafkaProducer<String, String> producer;
    private ObjectMapper objectMapper = null;

    public String topic;

    public SyncProducerEvent(@Value("${spring.kafka.bootstrap-servers}")String bootstrapServers,  @Value("${spring.kafka.topic}")String topic){
        this.topic = topic;
        this.objectMapper = new ObjectMapper();


        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(prop);
    }

    public void sendSync(JRS_Registration jrsRegistration){
        try {
            String json = objectMapper.writeValueAsString(jrsRegistration);
            System.out.println("The Email is:"+jrsRegistration.getEmail());
            ProducerRecord<String, String>  record = new ProducerRecord<>(topic,jrsRegistration.getEmail(),json);

            //synchronous send
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("Sent to topic:" + metadata.topic()+",partition:"+metadata.partition());
        }catch(JsonProcessingException | ExecutionException | InterruptedException e){
            throw new RuntimeException(e);
        }

    }

    public void sendQuery(String query){
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, "Get Users with Email", query);
            RecordMetadata metadata = producer.send(record).get();
        }catch(ExecutionException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public void close(){
        producer.close();
    }



}
