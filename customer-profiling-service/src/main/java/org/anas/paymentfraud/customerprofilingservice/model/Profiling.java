package org.anas.paymentfraud.customerprofilingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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

//    private long totalAmount; // extra
//    private Transaction lastestTransaction ;
}