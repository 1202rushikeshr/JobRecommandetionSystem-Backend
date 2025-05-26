package com.ai.JobRecommendationSystem.service;


import com.ai.JobRecommendationSystem.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Service
public class JRSProducerService {

    private KafkaProducer<String, User> producer;
    private KafkaProducer<String, String> producer1;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.query-topic}")
    private String queryTopic;

       public JRSProducerService(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers, @Value("${spring.kafka.schemaregistryurl}") String schemaRegistryUrl){
        Properties prop = new Properties();
           Properties prop1 = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//           prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//           prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prop.put("value.serializer", "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer");
        prop.put("schema.registry.url", schemaRegistryUrl);

           prop1.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
           prop1.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
           prop1.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<String, User>(prop);
        this.producer1 = new KafkaProducer<String, String>(prop1);

    }

    public void sendSync(User jrsRegistration){
        try {
//            String json = objectMapper.writeValueAsString(jrsRegistration);
//            System.out.println("The Email is:"+jrsRegistration.getEmail());
            ProducerRecord<String, User>  record = new ProducerRecord<>(topic,jrsRegistration.getEmail(),jrsRegistration);

                       //synchronous send
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("Sent to topic:" + metadata.topic()+",partition:"+metadata.partition());
        }catch(ExecutionException | InterruptedException e){
            throw new RuntimeException(e);
        }

    }

    public void sendQuery(String query){
        try {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(queryTopic, "Get Users with Email", query);
            RecordMetadata metadata = producer1.send(record).get();
        }catch(ExecutionException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    //close kafka-client cleanly
    @PreDestroy
    public void close(){
        producer.close();
    }



}
