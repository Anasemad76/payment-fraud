package org.anas.paymentfraud.customerprofilingservice.stream;

import org.anas.paymentfraud.customerprofilingservice.config.JsonSerde;
import org.anas.paymentfraud.customerprofilingservice.model.Profiling;
import org.anas.paymentfraud.customerprofilingservice.model.Transaction;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@Component
public class CustomerProfilingProcessor {

    public KTable<String, Long> aggregateWindowedCount(KStream<String, Transaction> stream,Duration duration){

        return stream
                .groupByKey()
                .windowedBy(TimeWindows.of(duration))
                .count()
                .toStream((windowedKey, value) -> windowedKey.key())
                .groupByKey()
                .reduce((oldVal, newVal) -> newVal);
    }


    @Bean
    public KStream<String, Profiling> kStream(StreamsBuilder builder) {
        KStream<String, Transaction> stream = builder.stream("transaction-topic",
                Consumed.with(Serdes.String(), new JsonSerde<>(Transaction.class)));

        stream.foreach((k, v) -> {
            System.out.println(">>>>" +v);
        });

        // gets total tx amount fo each customer , can be used then to reject if tx will make total exceed limit
        KTable<String, Double> customerTotals=stream
                .groupByKey()
                .aggregate(
                        ()->0.0,
                        (clientIdKey,transaction,currentTotal)->currentTotal + transaction.getAmount(),
                        Materialized.with(Serdes.String(), Serdes.Double()) // stores data in disk

                );
        customerTotals.toStream().foreach((k,v)->{
            System.out.println("ClientId: " + k + " Total: " + v);
        });

//        profilingKStream<String, Double> customerTotals2=stream
//                .groupByKey()
//                .aggregate(
//                        Profiling::new,
//                        (clientIdKey,transaction,aggregate)->aggregate.setTotalAmount(transaction.getAmount()),
//                        Materialized.with(Serdes.String(),new JsonSerde<>(Profiling.class)) // stores data in disk
//                          .toStream();
//        profilingKStream.mapValues((readOnlykey,profiling)->profiling.getlastestTransaction())
//        .filter((key,value)->value.getState().equals(bankState.REJECTED)
//        .to(rejected-transaction-topic)


        // aggregate counts of 1 min window
        KTable<String, Long> oneMinCounts     = aggregateWindowedCount(stream, Duration.ofMinutes(1));
        KTable<String, Long> tenMinCounts     = aggregateWindowedCount(stream, Duration.ofMinutes(10));
        KTable<String, Long> oneHourCounts    = aggregateWindowedCount(stream, Duration.ofHours(1));
        KTable<String, Long> threeHourCounts  = aggregateWindowedCount(stream, Duration.ofHours(3));
        KTable<String, Long> oneDayCounts     = aggregateWindowedCount(stream, Duration.ofDays(1));


        KTable<String, Profiling> profilingTable = oneMinCounts.join(
                tenMinCounts,
                (oneMin, tenMin) -> {
                    Profiling profiling = new Profiling();
                    profiling.setTrxCountLastMinute(oneMin != null ? oneMin.intValue() : 0);
                    profiling.setTrxCountLast10Minutes(tenMin != null ? tenMin.intValue() : 0);
                    return profiling;
                }
        ).join(
                oneHourCounts,
                (profiling, oneHour) -> {
                    profiling.setTrxCountLastHour(oneHour != null ? oneHour.intValue() : 0);
                    return profiling;
                }
        ).join(
                threeHourCounts,
                (profiling, threeHour) -> {
                    profiling.setTrxCountLast3Hours(threeHour != null ? threeHour.intValue() : 0);
                    return profiling;
                }
        ).join(
                oneDayCounts,
                (profiling, oneDay) -> {
                    profiling.setTrxCountLast24Hours(oneDay != null ? oneDay.intValue() : 0);
                    return profiling;
                }
        );

        profilingTable.toStream().foreach((k, v) -> {
            System.out.println("Profiling: " + k + " " + v);
        });


        //verify in console
//        oneMinCounts.toStream().foreach((k,v)->{
//
//            System.out.println("One min window key : "+k+" count: " + v);
//        });
//        tenMinCounts.toStream().foreach((k,v)->{
//            System.out.println("Ten min window key : "+k+" count: " + v);
//        });

//        oneHourCounts.toStream().foreach((k,v)->{
//            System.out.println("window key : "+k+" count: " + v);
//        });



        KStream<String, Profiling> profilingStream = profilingTable.toStream();

        // Send to Kafka topic
        profilingStream.to("customer-profile", Produced.with(Serdes.String(), new JsonSerde<>(Profiling.class)));

        // Return the stream (for testing or further processing)
        return profilingStream;



    }
}
