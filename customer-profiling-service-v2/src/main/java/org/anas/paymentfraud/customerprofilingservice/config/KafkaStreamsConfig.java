package org.anas.paymentfraud.customerprofilingservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
public class KafkaStreamsConfig {

//    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
//    public KafkaStreamsConfiguration kafkaStreamsConfiguration() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "customer-profiling-app");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "ENTRA-184:9092");
////        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
////        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
//        return new KafkaStreamsConfiguration(props);
//    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        return new KafkaStreamsConfiguration(buildStreamProperties());}
    private Map<String, Object> buildStreamProperties() {
        Map<String, Object> props = new HashMap<>();    // Core configuration
        props.put(APPLICATION_ID_CONFIG, "customer-profiling-app-v55"); // acts like a group id
        props.put(BOOTSTRAP_SERVERS_CONFIG,"ENTRA-184:9092");
//        props.put(STATE_DIR_CONFIG, stateDir);
        // Serde configuration
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        // Json Transaction Serde
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());

        // Processing configuration(Batching)
//        props.put(NUM_STREAM_THREADS_CONFIG, 2);
        props.put(CACHE_MAX_BYTES_BUFFERING_CONFIG,10485760); // cache in memory before flushing and wiriting to rocksdb and changelog
        props.put(COMMIT_INTERVAL_MS_CONFIG, 1000); // if cache size is still small , flush after certain interval


        // Reliability configuration
          props.put(DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,"org.apache.kafka.streams.errors.LogAndContinueExceptionHandler");
          props.put(NUM_STANDBY_REPLICAS_CONFIG, 1); // a standby instance of kafka stream app must be created inorder for this to work
          props.put(REPLICATION_FACTOR_CONFIG, 1);
          props.put(AUTO_OFFSET_RESET_CONFIG, "latest"); //tells what to do when there is no valid-committed offset for a partition

        // Monitoring configuration
         props.put(METRICS_RECORDING_LEVEL_CONFIG, "INFO");

        // PERFORMANCE TUNING
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG,1); // Match topic partition count



        return props;
    }
}