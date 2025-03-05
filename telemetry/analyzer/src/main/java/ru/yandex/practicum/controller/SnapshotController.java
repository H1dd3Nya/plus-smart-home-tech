package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.handler.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SnapshotController {
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final KafkaConfig config;
    private final SnapshotHandler handler;


    public void start() {
        try {
            consumer.subscribe(List.of(config.getSnapshotTopicIn()));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(1000));
                if (records.isEmpty()) continue;
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    log.info("Получено сообщение: {}, со смещением {}, из топика {}",
                            record.value(), record.offset(), record.topic());
                    handler.handle(record.value());
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            consumer.wakeup();
        }
    }
}