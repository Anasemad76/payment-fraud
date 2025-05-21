
package org.anas.paymentfraud.customerprofilingservice.stream;

import lombok.extern.slf4j.Slf4j;
import org.anas.paymentfraud.customerprofilingservice.config.JsonSerde;
import org.anas.paymentfraud.customerprofilingservice.model.Profiling;
import org.anas.paymentfraud.customerprofilingservice.model.Transaction;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class CustomerProfilingProcessor {
//    public KTable<String, Long> aggregateWindowedCount(KStream<String, Transaction> stream, Duration duration) {
//        String storeName = "ten-min-count-store";
//
//        return stream
//                .groupByKey()
//                .windowedBy(TimeWindows.of(duration))
//                .count()
//                .toStream((windowedKey, value) -> windowedKey.key())
//                .groupByKey()
//                .reduce((oldVal, newVal) -> newVal,
//                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(storeName)
//                                .withKeySerde(Serdes.String())
//                                .withValueSerde(Serdes.Long())
//                                .withCachingEnabled()
//                                .withLoggingDisabled());
//    }
        public KStream<String, Long> aggregateWindowedCount(KStream<String, Transaction> stream,Duration duration){

        return stream
                .groupByKey()
                .windowedBy(TimeWindows.of(duration))
                .count()
                .toStream((windowedKey, value) -> windowedKey.key());
//                .groupByKey()
//                .reduce((oldVal, newVal) -> newVal);
    }

    @Bean
    public KStream<String, Profiling> kStream(StreamsBuilder builder) {
        // Define the profiling store
        StoreBuilder<KeyValueStore<String, Profiling>> profilingStore = Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore("profiling-store"),
                Serdes.String(),
                new JsonSerde<>(Profiling.class)
        ).withLoggingDisabled(); // this lines tells not to use a state store
        builder.addStateStore(profilingStore);

        // you can create a state store and a changelog to store results



        // Stream of transactions
        KStream<String, Transaction> stream = builder.stream("transaction-topic-3p",
                Consumed.with(Serdes.String(), new JsonSerde<>(Transaction.class)));

        /// //////////////////////////////////////////////////////


        KTable<String, Profiling> profilingTablev2 = stream
                .groupByKey()
                .aggregate( // aggregate is stateful
                        Profiling::new, // only if no old profiling exists for this key
                        (key, txn, profiling) -> ProfilingHelper.updateProfiling(profiling, txn),
                        Materialized.<String, Profiling, KeyValueStore<Bytes, byte[]>>as("profiling-store-v2") // name the store
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(Profiling.class))
                );// materialized will create state store(rocksdb in disk)
        // result of ktable is stored in cache and after commit interval it will be commited to changelog and state store
        //alongside with the offset commit(guarantees exactly or atleast once semantic)
        // compaction is used on changelog to only store latest key value pair for each key

        //////////////////////////////////////////////////////

        // Get the windowed counts as a KStream
        KStream<String, Long> oneMinCountsStream     = aggregateWindowedCount(stream, Duration.ofMinutes(1));
        KStream<String, Long> tenMinCountsStream     = aggregateWindowedCount(stream, Duration.ofMinutes(10));
        KStream<String, Long> oneHourCountsStream    = aggregateWindowedCount(stream, Duration.ofHours(1));
        KStream<String, Long> threeHourCountsStream  = aggregateWindowedCount(stream, Duration.ofHours(3));
        KStream<String, Long> oneDayCountsStream     = aggregateWindowedCount(stream, Duration.ofDays(1));



        // Apply transformer to the count stream instead of original stream

        KStream<String, Profiling> oneMinProfilingStream = oneMinCountsStream
                .transformValues(
                        () -> new CountProfilingTransformer("1min"),   // Optionally parameterize your transformer
                        Named.as("profiling-transformer-1min"),
                        "profiling-store"
                ).filter((key, value) -> value != null);

        KStream<String, Profiling> tenMinProfilingStream = tenMinCountsStream
                .transformValues(
                        () -> new CountProfilingTransformer("10min"),
                        Named.as("profiling-transformer-10min"),
                        "profiling-store"
                ).filter((key, value) -> value != null);

        KStream<String, Profiling> oneHourProfilingStream = oneHourCountsStream
                .transformValues(
                        () -> new CountProfilingTransformer("1hour"),
                        Named.as("profiling-transformer-1hour"),
                        "profiling-store"
                ).filter((key, value) -> value != null);
        KStream<String, Profiling> threeHourProfilingStream = threeHourCountsStream
                .transformValues(
                        () -> new CountProfilingTransformer("3hour"),
                        Named.as("profiling-transformer-3hour"),
                        "profiling-store"
                ).filter((key, value) -> value != null);
        KStream<String, Profiling> oneDayProfilingStream = oneDayCountsStream
                .transformValues(
                        () -> new CountProfilingTransformer("1day"),
                        Named.as("profiling-transformer-1day"),
                        "profiling-store"
                ).filter((key, value) -> value != null);

        KStream<String, Profiling> countResultStream = oneMinProfilingStream
                .merge(tenMinProfilingStream)
                .merge(oneHourProfilingStream)
                .merge(threeHourProfilingStream)
                .merge(oneDayProfilingStream);



        KStream<String, Profiling> completeProfilingStream = countResultStream
                .transform(  // Change to transform() instead of transformValues()
                        () -> new CompleteProfilingTransformer(),
                        Named.as("complete-profiling-transformer"),
                        "profiling-store-v2"
                );






        completeProfilingStream.to("customer-profile",
                Produced.with(Serdes.String(), new JsonSerde<>(Profiling.class)));

        System.out.println("===== TOPOLOGY DESCRIPTION =====");
        System.out.println(builder.build().describe());
        System.out.println("===== END TOPOLOGY =====");

        return completeProfilingStream;
    }
}
