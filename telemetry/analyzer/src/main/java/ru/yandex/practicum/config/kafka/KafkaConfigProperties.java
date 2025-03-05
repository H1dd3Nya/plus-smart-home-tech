package ru.yandex.practicum.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.config")
public class KafkaConfigProperties {
    private String bootstrapServers;
    private ConsumerProperties hubConsumer;
    private ConsumerProperties snapshotConsumer;

    private String hubEventsTopic;
    private String sensorSnapshotsTopic;
}