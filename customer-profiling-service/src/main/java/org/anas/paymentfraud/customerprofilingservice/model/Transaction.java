package org.anas.paymentfraud.customerprofilingservice.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Transaction {
//    private String transaction_id;
//    private String account_number;
//    private String customer_id;
//    private double amount;
//    private String currency;
//    private String payment_method;
//    private String payment_type;
//    private String transaction_date;
//    private String country;
//    private String city;
//    private String merchant_name;
//    private String status;
//
//
//}



import com.fasterxml.jackson.annotation.JsonProperty;




public class Transaction {

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("customer_id")
    private String customerId;

    private double amount;
    private String currency;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("transaction_date")
    private String transactionDate;

    private String country;
    private String city;

    @JsonProperty("merchant_name")
    private String merchantName;

    private String status;


    // Getters and setters for all fields
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}


