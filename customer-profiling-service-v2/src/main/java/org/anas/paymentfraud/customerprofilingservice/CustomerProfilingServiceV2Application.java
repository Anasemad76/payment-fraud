package org.anas.paymentfraud.customerprofilingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class CustomerProfilingServiceV2Application {

    public static void main(String[] args) {
        SpringApplication.run(CustomerProfilingServiceV2Application.class, args);
    }

}
