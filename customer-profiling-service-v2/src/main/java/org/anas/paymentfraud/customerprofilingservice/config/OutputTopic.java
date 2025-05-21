package org.anas.paymentfraud.customerprofilingservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class OutputTopic {

    @Bean
    public NewTopic customerProfilingTopic() {
        return TopicBuilder.name("customer-profile")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
