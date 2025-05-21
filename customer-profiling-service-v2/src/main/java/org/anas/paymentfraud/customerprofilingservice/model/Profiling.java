package org.anas.paymentfraud.customerprofilingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profiling {
    //design java an doython to have strructure deisng of field so it can be dynamic
    // template between java na python
    // learnging models on selection of field therefore you want to train the model on sprecifi fields
    // i want hte proto to be flexible and not  have every fields , depends on model need
    // meeting tom withAI
    private String clientId;
    private int trxCountLastMinute;
    private int trxCountLast10Minutes;
    private int trxCountLastHour;
    private int trxCountLast3Hours;
    private int trxCountLast24Hours;

//    @JsonFormat(pattern = "yyyy-MM-dd")
    private String last_debit_date;

//    @JsonFormat(pattern = "yyyy-MM-dd")
    private String last_credit_date;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String last_time_using_type;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String  last_time_using_credit;

    private int trx_count_since_last_credit;



    private double totalTxAmount; // extra

//    private Transaction lastestTransaction ;


    public boolean isComplete() {
        return trxCountLastMinute != 0 &&
                trxCountLast10Minutes != 0 &&
                trxCountLastHour != 0 &&
                trxCountLast3Hours != 0 &&
                trxCountLast24Hours != 0;
    }
}