package org.anas.paymentfraud.readerservice.csvtester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CsvRunner implements CommandLineRunner {

    @Autowired
    private CsvTransactionReader reader;

    @Override
    public void run(String... args) throws Exception {
        reader.readAndProduce("src/main/resources/sample_transactions.csv");
    }
}