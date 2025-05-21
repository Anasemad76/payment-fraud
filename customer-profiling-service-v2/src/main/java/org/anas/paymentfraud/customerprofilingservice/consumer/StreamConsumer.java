package org.anas.paymentfraud.customerprofilingservice.consumer;

import org.anas.paymentfraud.customerprofilingservice.model.Profiling;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StreamConsumer {
//    @KafkaListener(topics = "customer-profile", groupId = "engine-evaluation")
//    public void listen(ConsumerRecord<String, Profiling> record) {
//        Profiling profiling = record.value(); // Get the deserialized Profiling object
//        System.out.println("Inside CONSUMER -> Profiling: " + profiling);
//    }
}
