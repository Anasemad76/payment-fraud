package org.anas.paymentfraud.customerprofilingservice.stream;

import org.anas.paymentfraud.customerprofilingservice.model.Profiling;
import org.anas.paymentfraud.customerprofilingservice.model.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProfilingHelper {

    public static Profiling updateProfiling(Profiling profiling, Transaction txn){
        profiling.setTotalTxAmount(profiling.getTotalTxAmount() + txn.getAmount());

        LocalDate txnDate = LocalDate.parse(txn.getTransactionDate().substring(0,10));
        if(txn.getPaymentMethod().equalsIgnoreCase("debit card")){
            profiling.setLast_debit_date(txnDate.toString());
        }
        if (txn.getPaymentMethod().equalsIgnoreCase("credit card")) {
            profiling.setLast_credit_date(txnDate.toString());
            profiling.setTrx_count_since_last_credit(0);
            LocalDateTime txnDateTime = LocalDateTime.parse(txn.getTransactionDate(), DateTimeFormatter.ISO_DATE_TIME);
            profiling.setLast_time_using_credit(txnDateTime.toString());
        }else{
            profiling.setTrx_count_since_last_credit(profiling.getTrx_count_since_last_credit() + 1);
        }


        return profiling;

    }
}
