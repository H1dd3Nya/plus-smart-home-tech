package ru.practicum.aggregator.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorStarter {
    private final Consumer<String, SensorEventAvro> consumer;
    private final SensorEventHandler eventHandler;
    private final Producer<String, SpecificRecordBase> producer;
    @Value("${aggregator.topic.telemetry-snapshots}")
    private String snapshotsTopic;
    @Value("${topic.telemetry-sensors}")
    private String sensorsTopic;

    public void start() {
        try {
            consumer.subscribe(List.of(sensorsTopic));

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();
                    log.info("обрабатываем сообщение датчика {}", event);
                    Optional<SensorsSnapshotAvro> snapshot = eventHandler.updateState(event);
                    log.info("Получили снимок состояния {}", snapshot);
                    if (snapshot.isPresent()) {
                        log.info("запись снимка в топик Kafka");
                        ProducerRecord<String, SpecificRecordBase> message = new ProducerRecord<>(snapshotsTopic,
                                null, event.getTimestamp().toEpochMilli(), event.getHubId(), snapshot.get());

                        producer.send(message);
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close(Duration.ofSeconds(10));
            }
        }
    }
}
