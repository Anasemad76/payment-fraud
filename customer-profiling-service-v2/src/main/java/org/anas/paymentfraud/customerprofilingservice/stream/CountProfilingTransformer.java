package org.anas.paymentfraud.customerprofilingservice.stream;



import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.anas.paymentfraud.customerprofilingservice.model.Profiling;
import org.anas.paymentfraud.customerprofilingservice.model.Transaction;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.KafkaStreams;

//public class ProfilingTransformer implements ValueTransformerWithKey<String, Transaction, Profiling> { //<K,V,R>
//
//    private KeyValueStore<String, Profiling> profilingStateStore;
//    private KeyValueStore<String, Long> windowedCountStore;
//
//    @Override
//    public void init(ProcessorContext context) {
//        // Access the profiling state store
//        this.profilingStateStore = (KeyValueStore<String, Profiling>) context.getStateStore("profiling-store");
//
//        // Access the windowed count store
//        this.windowedCountStore = (KeyValueStore<String, Long>) context.getStateStore("windowed-count-store");
//    }
//
//    @Override
//    public Profiling transform(String key, Transaction transaction) {
//        // Retrieve existing profiling data from the state store
//        Profiling profiling = profilingStateStore.get(key);
//        System.out.println(">>> HERE 1 :"+profiling);
//        if (profiling == null) {
//            profiling = new Profiling();  // Create a new profiling object if none exists
//        }
//
////        // Enrich the profiling data with transaction information
////        profiling.setLastTransactionAmount(transaction.getAmount());
//
//        // Get the windowed count for the key (1-minute window in this case)
//        Long windowedCount = windowedCountStore.get(key);
//        System.out.println(">>> HERE 2 :"+windowedCount);
//        if (windowedCount != null) {
//            profiling.setTrxCountLastMinute(windowedCount.intValue());  // Set the count from windowed store
//        } else {
//            profiling.setTrxCountLastMinute(0);  // Default to 0 if no count exists
//        }
//
//        return profiling;
//    }
//
//    @Override
//    public void close() {
//        // Cleanup if needed (e.g., close connections)
//    }
//}

import org.apache.kafka.streams.state.ValueAndTimestamp;

public class CountProfilingTransformer implements ValueTransformerWithKey<String, Long, Profiling> {
    private KeyValueStore<String, Profiling> profilingStateStore;
    private final String windowName;

    public CountProfilingTransformer(String windowName) {
        this.windowName = windowName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.profilingStateStore = (KeyValueStore<String, Profiling>) context.getStateStore("profiling-store");
    }

    @Override
    public Profiling transform(String key, Long count) {  // Now receiving count directly
        // Retrieve or create profiling data
        Profiling profiling = profilingStateStore.get(key);
        System.out.println("profiling: " + profiling);
        if (profiling == null) {
            profiling = new Profiling();
            profiling.setClientId(key);
        }

        // Use count directly since we're subscribed to the count stream
        System.out.println(">>> Window: " +windowName+", Client ID: " + key + ", Current count: " + count );
        switch (windowName) {
            case "1min" -> profiling.setTrxCountLastMinute(count.intValue());
            case "10min" -> profiling.setTrxCountLast10Minutes(count.intValue());
            case "1hour" -> profiling.setTrxCountLastHour(count.intValue());
            case "3hour" -> profiling.setTrxCountLast3Hours((count.intValue()));
            case "1day" -> profiling.setTrxCountLast24Hours(count.intValue());
            default -> {
                // handle unknown window
            }
        }
        // Store the updated profiling
        profilingStateStore.put(key, profiling);

        if (profiling.isComplete()) {
            System.out.println(" Emitting COMPLETE profiling for: " + key);
            profilingStateStore.delete(key);
            // not the best way as it will delete entry in changelog too
            // and if needed it then u have to query other state stores
            return profiling;
        } else {
            return null;
        }
    }

    @Override
    public void close() {
    }
}