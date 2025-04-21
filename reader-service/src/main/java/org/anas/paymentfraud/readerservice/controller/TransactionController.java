package org.anas.paymentfraud.readerservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.anas.paymentfraud.readerservice.model.Transaction;
import org.anas.paymentfraud.readerservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    //kafka

    private TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping
    public ResponseEntity<String> postTransaction(@RequestBody Transaction transaction) {

            // validation therefore you will need to work with tx as a class
            //  thats why i did reqbody not string to have obj and do operations on
            System.out.println(">>>>>"+ transaction);
            boolean isSuccessful=transactionService.sendTransaction(transaction);
            if (isSuccessful){
                return ResponseEntity.ok("Transaction sent successfully");
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send transaction");
            }

    }





}
