package org.anas.paymentfraud.customerprofilingservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerde<T> implements Serde<T> {
    private final Class<T> clazz;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonSerde(Class<T> clazz) {
        this.clazz = clazz;
    }
    @Override
//    @SneakyThrows
    public Serializer<T> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<T> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(data, clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
