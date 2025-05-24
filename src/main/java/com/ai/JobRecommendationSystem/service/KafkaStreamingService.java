package com.ai.JobRecommendationSystem.service;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Properties;

@Service
public class KafkaStreamingService {
    @Value("${spring.datasource.url}")
    private static String connectionURL;

    @Value("${spring.datasource.username}")
    private static String dbUsername;

    @Value("${spring.datasource.password}")
    private static String dbPassword;

    @Value("${spring.kafka.topic}")
    private String topic;



    public void createKafkaStream(@Value("${spring.kafka.bootstrap-servers}")String bootstrapServers, @Value("${pring.kafka.query-topic}")String queryTopic) {
        Properties prop = new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "jrs-application");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KafkaStreams streams = new KafkaStreams(builder.build(),prop);
        streams.start();
        KStream<String, String> queries = builder.stream(queryTopic);
        KStream<String,String> results = queries.mapValues(query -> {
            try{
                return executeQuery(query);
            }catch(Exception e){
                return "Error"+ e.getMessage();
            }
        });

        results.to(topic);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static String executeQuery(String query) throws SQLException {
        StringBuilder result = new StringBuilder();
        try(Connection con = DriverManager.getConnection(connectionURL,dbUsername,dbPassword);
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(query)){
            int columnCount = res.getMetaData().getColumnCount();
            while(res.next()){
                for(int i= 1; i <= columnCount; i++){
                    result.append(res.getString(i));
                }
                result.append("\n");
            }
        }

        return result.toString();
    }
}
