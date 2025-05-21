package org.anas.paymentfraud.readerservice.csvtester;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCSV {
    private String transactionId;
    private String accountNumber;
    private String customerId;
    private double amount;
    private String currency;
    private String paymentMethod;
    private String paymentType;
    private String transactionDate;
    private String country;
    private String city;
    private String merchantName;
    private String status;

}
