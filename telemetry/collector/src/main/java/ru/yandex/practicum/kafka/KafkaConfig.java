package ru.yandex.practicum.kafka;

import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.serialization.AvroSerializer;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    private KafkaProducer<String, SpecificRecordBase> producer;

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaProducer() {
        producer = new KafkaProducer<>(getPropertiesSensor());
        return producer;
    }

    private Properties getPropertiesSensor() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
        return config;
    }

    @PreDestroy
    public void closeProducer() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}
