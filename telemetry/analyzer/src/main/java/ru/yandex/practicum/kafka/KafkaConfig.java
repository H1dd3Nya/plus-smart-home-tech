package ru.yandex.practicum.kafka;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfig {
    public ProducerConfig producer;
    public ConsumerConfig consumer;
    private Properties hubConsumerProperties;
    private Properties snapshotConsumerProperties;
    private Map<String, String> topics;
}
