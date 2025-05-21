package org.anas.paymentfraud.storeservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.anas.paymentfraud.storeservice.model.Transaction;
import org.anas.paymentfraud.storeservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;








@Service
public class KafkaConsumerService {

    @Autowired
    private TransactionRepository repository;
//    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "transaction-topic-1", groupId = "store-service-group")
    public void consume(Transaction transaction) throws JsonProcessingException {

//        performance problem (kafka doesnt use jackson)
//        make it deserialize on kafka's level
//        Transaction transaction1=objectMapper.readValue(transaction,Transaction.class);

//        commented this for test metrics only
//        repository.save(transaction);
        System.out.println("Listener received : "+transaction);

    }

    // Consumer 1 - listens to partition 0
    @KafkaListener(
            topics = "transaction-topic-2",
            groupId = "store-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen1(Transaction message) {
        System.out.println("Consumer 1 received: " + message);
    }

    // Consumer 2 - listens to partition 1
    @KafkaListener(
            topics = "transaction-topic-2",
            groupId = "store-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen2(Transaction message) {
        System.out.println("Consumer 2 received: " + message);
    }

    // Consumer 3 - listens to partition 2
    @KafkaListener(
            topics = "transaction-topic-2",
            groupId = "store-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen3(Transaction message) {
        System.out.println("Consumer 3 received: " + message);
    }
}
