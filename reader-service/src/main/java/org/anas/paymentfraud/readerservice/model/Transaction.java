package org.anas.paymentfraud.readerservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
// check loombok

public class Transaction {
    private String transaction_id;
    private String account_number;
    private String customer_id;
    private double amount;
    private String currency;
    private String payment_method;
    private String payment_type;
    private String transaction_date;
    private String country;
    private String city;
    private String merchant_name;
    private String status;


}
