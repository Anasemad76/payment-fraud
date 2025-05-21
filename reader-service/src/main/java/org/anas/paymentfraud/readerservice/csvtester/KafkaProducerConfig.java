package org.anas.paymentfraud.readerservice.csvtester;


import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Component("uniqueKafkaProducerConfig")
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, TransactionCSV> producerFactory1() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "ENTRA-184:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.CLIENT_ID_CONFIG, "reader-producer"); // Important for metrics!

        // Tuning the producer for better performance:

        //Controls how much data the producer will batch together before sending it.
        // 1. Increase batch size to 100000–200000 bytes (default 16384)
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 100000); // Adjust this value as needed (100000–200000)

        //Forces the producer to wait this long before sending a batch, even if it's not full.
        // 2. Increase linger.ms to 10–100 milliseconds (default 0)
        config.put(ProducerConfig.LINGER_MS_CONFIG, 50); // Adjust this value as needed (10–100)

        //Compresses message batches to reduce network load.
        // 3. Use LZ4 compression (default none)
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        //Waits for just 1 broker to acknowledge, instead of all.
        // 4. Set acks to 1 (default is 'all', which waits for all replicas)
        config.put(ProducerConfig.ACKS_CONFIG, "1");


        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean(name = "csvKafkaTemplate")
    public KafkaTemplate<String, TransactionCSV> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory1());
    }
}

