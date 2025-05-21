package org.anas.paymentfraud.readerservice.csvtester;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Configuration
@Component("uniqueKafkaTopicConfig")
public class KafkaTopicConfig {

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("transaction-topic-2")
                .partitions(3)           // Specify the number of partitions
                .replicas(1)              // Specify the replication factor
                .build();
    }
}
