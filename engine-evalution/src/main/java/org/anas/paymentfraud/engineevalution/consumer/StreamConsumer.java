package org.anas.paymentfraud.engineevalution.consumer;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.anas.paymentfraud.engineevalution.model.Profiling;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import transaction.Transaction;
import transaction.TransactionServiceGrpc;

@Component
public class StreamConsumer {
    public int once=0;
    @KafkaListener(topics = "customer-profile", groupId = "engine-evaluation")
    public void listen(ConsumerRecord<String, Profiling> record) {
        Profiling profiling = record.value(); // Get the deserialized Profiling object
        System.out.println("Inside CONSUMER -> Profiling: " + profiling);
        if (once==0) {
            sendToAIModel(profiling);
        }
    }

    private void sendToAIModel(Profiling profiling) {
        once++;
        ManagedChannel channelBuilder = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();


        TransactionServiceGrpc.TransactionServiceBlockingStub txSerivceBlockingStub = TransactionServiceGrpc.newBlockingStub(channelBuilder);

        Transaction.TransactionFrequency request = Transaction.TransactionFrequency
                .newBuilder().
                setCustomerId(profiling.getClientId())
                .setTrx1M(profiling.getTrxCountLastMinute())
                .setTrx10M(profiling.getTrxCountLast10Minutes())
                .setTrx1H(profiling.getTrxCountLastHour())
                .setTrx3H(profiling.getTrxCountLast3Hours())
                .setTrx24H(profiling.getTrxCountLast24Hours())
                .build();
        Transaction.PredictionResult response = txSerivceBlockingStub.predict(request);
        System.out.println("Response inside client : " + response);
    }
}
