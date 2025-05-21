package org.anas.paymentfraud.customerprofilingservice.stream;

import org.anas.paymentfraud.customerprofilingservice.model.Profiling;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;


public class CompleteProfilingTransformer implements Transformer<String, Profiling, KeyValue<String, Profiling>> {

    private KeyValueStore<String, ValueAndTimestamp<Profiling>> profilingStore;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.profilingStore = (KeyValueStore<String, ValueAndTimestamp<Profiling>>)  context.getStateStore("profiling-store-v2");
    }

    @Override
    public KeyValue<String, Profiling> transform(String key, Profiling partialProfiling) {
        // Read event profiling from profiling store
        System.out.println("Partial profiling: " + partialProfiling);
        ValueAndTimestamp<Profiling> eventProfilingWithTimestamp = profilingStore.get(key);
        Profiling eventProfiling = (eventProfilingWithTimestamp != null) ?
                eventProfilingWithTimestamp.value() : new Profiling();
        System.out.println("eventProfiling: " + eventProfiling);

        // Create new Profiling object to hold the complete info
        Profiling fullProfiling = new Profiling();

        // Copy counts from partial profiling
        fullProfiling.setClientId(partialProfiling.getClientId());
        fullProfiling.setTrxCountLastMinute(partialProfiling.getTrxCountLastMinute());
        fullProfiling.setTrxCountLast10Minutes(partialProfiling.getTrxCountLast10Minutes());
        fullProfiling.setTrxCountLastHour(partialProfiling.getTrxCountLastHour());
        fullProfiling.setTrxCountLast3Hours(partialProfiling.getTrxCountLast3Hours());
        fullProfiling.setTrxCountLast24Hours(partialProfiling.getTrxCountLast24Hours());

        // Copy fields from event-driven profiling store
        fullProfiling.setLast_debit_date(eventProfiling.getLast_debit_date());
        fullProfiling.setLast_credit_date(eventProfiling.getLast_credit_date());
        fullProfiling.setLast_time_using_type(eventProfiling.getLast_time_using_type());
        fullProfiling.setLast_time_using_credit(eventProfiling.getLast_time_using_credit());
        fullProfiling.setTrx_count_since_last_credit(eventProfiling.getTrx_count_since_last_credit());
        fullProfiling.setTotalTxAmount(eventProfiling.getTotalTxAmount());

        return KeyValue.pair(key, fullProfiling);
    }

    @Override
    public void close() {
        // No-op
    }
}