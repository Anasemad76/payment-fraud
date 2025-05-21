package org.anas.paymentfraud.engineevalution.config;

import org.anas.paymentfraud.engineevalution.model.Profiling;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConsumerConfig {
    @Bean
    public ConsumerFactory<String, Profiling> consumerFactory() {
        JsonDeserializer<Profiling> deserializer = new JsonDeserializer<>(Profiling.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        deserializer.addTrustedPackages("*");  // Allow deserialization of any packages if necessary
        deserializer.setUseTypeHeaders(false); // Disable type headers if not used

        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"ENTRA-184:9092");
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "engine-evaluation-v1");
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,deserializer);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

//    @Bean
//    public ConcurrentMessageListenerContainer<String, Profiling> messageListenerContainer() {
//        return new ConcurrentMessageListenerContainer<>(consumerFactory(), new KafkaMessageListenerContainer<>(consumerFactory()));
//    }
@Bean
public ConcurrentKafkaListenerContainerFactory<String, Profiling> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Profiling> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
}
}
