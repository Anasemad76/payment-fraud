package org.anas.paymentfraud.customerprofilingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Profiling {
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


//    private long totalAmount; // extra
//    private Transaction lastestTransaction ;


}