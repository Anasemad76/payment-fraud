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

    @KafkaListener(topics = "transaction-topic", groupId = "store-service-group")
    public void consume(Transaction transaction) throws JsonProcessingException {

//        performance problem (kafka doesnt use jackson)
//        make it deserialize on kafka's level
//        Transaction transaction1=objectMapper.readValue(transaction,Transaction.class);
        repository.save(transaction);
        System.out.println("Listener received : "+transaction);

    }
}
