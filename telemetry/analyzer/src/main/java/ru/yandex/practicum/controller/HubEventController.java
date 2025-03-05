package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.handler.HubHandler;
import ru.yandex.practicum.handler.HubHandlerEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class HubEventController implements Runnable {
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final KafkaConfig config;
    private final HubHandler hubHandler;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(config.getHubTopicIn()));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records =
                        consumer.poll(Duration.ofMillis(1000));
                if (records.isEmpty()) continue;
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    log.info("Получено сообщение: {}, со смещением {}, из топика {}",
                            record.value(), record.offset(), record.topic());
                    HubHandlerEvent handler = hubHandler.getContext()
                            .get(record.value().getPayload().getClass().getName());
                    if (handler != null) {
                        handler.handle(record.value());
                        consumer.commitSync();
                    }
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