package org.anas.paymentfraud.readerservice.csvtester;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;

@Component
public class CsvTransactionReader {

    @Autowired
    private TransactionProducer producer;

    public void readAndProduce(String filePath) throws IOException {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withSkipLines(1)
                .build()) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                TransactionCSV txn = new TransactionCSV();
                txn.setTransactionId(line[0]);
                txn.setAccountNumber(line[1]);
                txn.setCustomerId(line[2]);
                txn.setAmount(Double.parseDouble(line[3]));
                txn.setCurrency(line[4]);
                txn.setPaymentMethod(line[5]);
                txn.setPaymentType(line[6]);
                txn.setTransactionDate(line[7]);
                txn.setCountry(line[8]);
                txn.setCity(line[9]);
                txn.setMerchantName(line[10]);
                txn.setStatus(line[11]);

                producer.sendTransaction(txn);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}

