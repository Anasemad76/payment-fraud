package org.anas.paymentfraud.readerservice.service;

import org.anas.paymentfraud.readerservice.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
// i guess you should verify input here before sending in kafka
public class TransactionService {

    private KafkaTemplate<String, Transaction> kafkaTemplate;

    @Autowired
    public TransactionService(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public boolean sendTransaction(Transaction transaction) {
        try {

            kafkaTemplate.send("transaction-topic", transaction.getCustomer_id(),transaction);
            System.out.println("Sending transaction: " + transaction);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }
}
