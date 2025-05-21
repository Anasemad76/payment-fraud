package org.anas.paymentfraud.storeservice.config;

import org.anas.paymentfraud.storeservice.model.Transaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

//    @Value("${spring.kafka.bootstrap-servers}")
//    private String server;
    @Bean
    public ConsumerFactory<String, Transaction> consumerFactory() {
        JsonDeserializer<Transaction> deserializer = new JsonDeserializer<>(Transaction.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"ENTRA-184:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "store-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,deserializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //Minimum bytes to fetch
        //This parameter sets the minimum number of bytes expected for a fetch
        // response from a consumer. Increasing this also reduces the number of
        // fetch requests made to Confluent Cloud, reducing the broker CPU overhead
        // to process each fetch, thereby also improving throughput.
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024 * 1024);


        //there may be a resulting trade-off to higher latency when increasing this parameter on the consumer.
        // This is because the broker won’t send the consumer new messages until
        // the fetch request has enough messages to fulfill the size of the fetch request
        // Wait up to 100ms to fill fetch
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 100);


        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Transaction> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Transaction> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
