package ru.yandex.practicum.config.kafka;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.serialization.HubDeserializer;
import ru.yandex.practicum.serialization.SnapshotDeserializer;

import java.util.Properties;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConfig {
    @Value("${spring.kafka.bootstrapServer}")
    String bootstrapServer;
    @Value("${spring.kafka.snapshot.group.id}")
    String snapchotGroupId;
    @Getter
    @Value("${spring.kafka.snapshot.topic.in}")
    String snapshotTopicIn;

    @Value("${spring.kafka.hub.group.id}")
    String hubGroupId;
    @Getter
    @Value("${spring.kafka.hub.topic.in}")
    String hubTopicIn;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> getSnapshotConsumer() {
        Properties configProps = new Properties();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, snapchotGroupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SnapshotDeserializer.class);
        return new KafkaConsumer<>(configProps);
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> getHubConsumer() {
        Properties configProps = new Properties();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, hubGroupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubDeserializer.class);
        return new KafkaConsumer<>(configProps);
    }
}