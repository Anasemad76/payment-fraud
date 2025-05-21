package org.anas.paymentfraud.readerservice.csvtester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private int count=0;
    @Autowired
    private KafkaTemplate<String, TransactionCSV> kafkaTemplate;



    public void sendTransaction(TransactionCSV transaction) {
//        kafkaTemplate.send("transaction-topic-1p",transaction.getCustomerId(), transaction);
        kafkaTemplate.send("transaction-topic-3p",transaction.getCustomerId(), transaction);
        count++;
        System.out.println("Sending transaction: " + count );
    }
}

