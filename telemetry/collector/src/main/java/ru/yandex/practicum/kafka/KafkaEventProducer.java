package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    public <T extends SpecificRecordBase> void sendToKafka(T event, String topic, String hubId, long timestamp) {
        Header header = new RecordHeader("timestamp", BigInteger.valueOf(timestamp).toByteArray());
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, hubId, event, List.of(header));

        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке сообщения в Kafka: {}", exception.getMessage());
            } else {
                log.info("Сообщение отправлено в Kafka: topic={}, partition={}, offset={}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
