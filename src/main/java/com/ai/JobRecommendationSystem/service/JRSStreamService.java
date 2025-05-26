package com.ai.JobRecommendationSystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
public class JRSStreamService {

    List<Map<String, Object>> resultList = new ArrayList<>();
    @Value("${spring.datasource.url}")
    private String connectionURL;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.query-topic}")
    private String queryTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private KafkaStreams streams;

    @PostConstruct
    public void createKafkaStream() {

        if (queryTopic == null || queryTopic.isBlank()) {
            throw new IllegalArgumentException("queryTopic is null or empty!");
        }
        Properties prop = new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "jrs-application");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> queries = builder.stream(queryTopic);
        KStream<String,String> results = queries.mapValues(query -> {
            try{
                return executeQuery(query);
            }catch(Exception e){
                return "Error"+ e.getMessage();
            }
        });
        results.to(topic);
        streams = new KafkaStreams(builder.build(),prop);
        streams.start();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (streams != null) streams.close();
        }));
    }

    private String executeQuery(String query) throws SQLException, JsonProcessingException {
        StringBuilder result = new StringBuilder();
        try(Connection con = DriverManager.getConnection(connectionURL,dbUsername,dbPassword);
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(query)){
            int columnCount = res.getMetaData().getColumnCount();
            while(res.next()){
                Map<String, Object> row = new HashMap<>();
                for(int i = 1; i <= columnCount; i++){
                    row.put(res.getMetaData().getColumnLabel(i), res.getObject(i));
                }
                resultList.add(row);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(resultList);
    }

    @PreDestroy
    public void shutdown() {
        if (streams != null) {
            streams.close();
        }
    }
}
