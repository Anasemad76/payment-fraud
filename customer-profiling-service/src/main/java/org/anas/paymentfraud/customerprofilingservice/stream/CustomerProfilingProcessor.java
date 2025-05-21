package org.anas.paymentfraud.customerprofilingservice.stream;

import org.anas.paymentfraud.customerprofilingservice.config.JsonSerde;
import org.anas.paymentfraud.customerprofilingservice.model.Profiling;
import org.anas.paymentfraud.customerprofilingservice.model.Transaction;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.kstream.Materialized;

import static org.anas.paymentfraud.customerprofilingservice.stream.ProfilingHelper.updateProfiling;

@Component
public class CustomerProfilingProcessor {

    public KTable<String, Long> aggregateWindowedCount(KStream<String, Transaction> stream,Duration duration){

        return stream
                .groupByKey()
                .windowedBy(TimeWindows.of(duration)) // repartition happens
                .count()//state store and changelog happens
                .toStream((windowedKey, value) -> windowedKey.key())
                .groupByKey() // repartition happens
                .reduce((oldVal, newVal) -> newVal);
    }


    @Bean
    public KStream<String, Profiling> kStream(StreamsBuilder builder) {
        KStream<String, Transaction> stream = builder.stream("transaction-topic-3p",
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



        KTable<String, Profiling> profilingTablev2 = stream
                .groupByKey()
                .aggregate( // aggregate is stateful
                        Profiling::new, // only if no old profiling exists for this key
                        (key, txn, profiling) -> ProfilingHelper.updateProfiling(profiling, txn),
                        Materialized.with(Serdes.String(), new JsonSerde<>(Profiling.class))
                );




        // aggregate counts of 1 min window
        KTable<String, Long> oneMinCounts     = aggregateWindowedCount(stream, Duration.ofMinutes(1));
        KTable<String, Long> tenMinCounts     = aggregateWindowedCount(stream, Duration.ofMinutes(10));
        KTable<String, Long> oneHourCounts    = aggregateWindowedCount(stream, Duration.ofHours(1));
        KTable<String, Long> threeHourCounts  = aggregateWindowedCount(stream, Duration.ofHours(3));
        KTable<String, Long> oneDayCounts     = aggregateWindowedCount(stream, Duration.ofDays(1));

        // Join KTables to build Profiling
        JsonSerde<Profiling> profilingSerde = new JsonSerde<>(Profiling.class);
        KTable<String, Profiling> profilingTable = oneMinCounts
                .join(tenMinCounts,
                        (oneMin, tenMin) -> {
                            Profiling profiling = new Profiling();
                            profiling.setTrxCountLastMinute(oneMin != null ? oneMin.intValue() : 0);
                            profiling.setTrxCountLast10Minutes(tenMin != null ? tenMin.intValue() : 0);
                            return profiling;
                        }
                        ,Materialized.with(Serdes.String(), profilingSerde)
                )
                .join(oneHourCounts,
                        (profiling, oneHour) -> {
                            profiling.setTrxCountLastHour(oneHour != null ? oneHour.intValue() : 0);
                            return profiling;
                        }
                        ,Materialized.with(Serdes.String(), profilingSerde)
                        )
                .join(threeHourCounts,
                        (profiling, threeHour) -> {
                            profiling.setTrxCountLast3Hours(threeHour != null ? threeHour.intValue() : 0);
                            return profiling;
                        }
                        ,Materialized.with(Serdes.String(), profilingSerde)
                )
                .join(oneDayCounts,
                        (profiling, oneDay) -> {
                            profiling.setTrxCountLast24Hours(oneDay != null ? oneDay.intValue() : 0);
                            return profiling;
                        }
                        ,Materialized.with(Serdes.String(), profilingSerde)
                )

                .join(profilingTablev2,  // async join (the problem)
                        (profiling, eventProfiling) -> {


                            // Set timestamps (from profilingTablev2)
                            profiling.setLast_debit_date(eventProfiling.getLast_debit_date());
                            profiling.setLast_credit_date(eventProfiling.getLast_credit_date());
                            profiling.setLast_time_using_type(eventProfiling.getLast_time_using_type());
                            profiling.setLast_time_using_credit(eventProfiling.getLast_time_using_credit());
                            profiling.setTrx_count_since_last_credit(eventProfiling.getTrx_count_since_last_credit());

                            return profiling;
                        }
                        ,Materialized.with(Serdes.String(),profilingSerde)
                );


//
//        // Suppress updates to emit one record per customer
        KStream<String, Profiling> resultStream =profilingTable
                .toStream()
                .groupByKey(Grouped.with(Serdes.String(), profilingSerde))
                .reduce((v1, v2) -> v2, Materialized.<String, Profiling, KeyValueStore<Bytes, byte[]>>as("profiling-final")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(profilingSerde))
                .toStream();
//                .foreach((k, v) -> System.out.println("Profiling: " + k + " " + v));
////                .to("customer-profiles-topic", Produced.with(Serdes.String(), profilingSerde));
             resultStream.to("customer-profile", Produced.with(Serdes.String(), profilingSerde));

        System.out.println("===== TOPOLOGY DESCRIPTION =====");
        System.out.println(builder.build().describe());
        System.out.println("===== END TOPOLOGY =====");

        return resultStream;


/// ////////////////////////////////////////////////////////////////////////////////////////////////////


//        KStream<String, Profiling> resultStream = profilingTable.toStream();
//        resultStream.to("customer-profile", Produced.with(Serdes.String(), profilingSerde));
//        return resultStream;

    }
}
