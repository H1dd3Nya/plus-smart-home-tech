package ru.yandex.practicum.handlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.services.ScenarioService;
import ru.yandex.practicum.services.SensorService;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private static final Duration CONSUMER_TIMEOUT = Duration.ofMillis(1000);
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final SensorService sensorService;
    private final ScenarioService scenarioService;
    private final List<String> topics = Collections.singletonList("telemetry.hubs.v1");

    public HubEventProcessor(KafkaConfig kafkaConfig, SensorService sensorService, ScenarioService scenarioService) {
        this.sensorService = sensorService;
        this.scenarioService = scenarioService;
        this.consumer = new KafkaConsumer<>(kafkaConfig.getHubConsumerProperties());
    }

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(topics);
            log.info("Слушаем топик с устройствами хаба");
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(CONSUMER_TIMEOUT);
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    try {
                        processing(record);
                    } catch (Exception e) {
                        log.error("При обработке сообщения произошла ошибка: {}", record, e);
                    }
                }
                try {
                    consumer.commitSync();
                } catch (Exception e) {
                    log.error("Ошибка фиксации", e);
                }
            }
        } catch (WakeupException ignored) {
            log.info("Остановка потребителя");
        } catch (Exception e) {
            log.error("Ошибка во время обработки получения устройств хаба", e);
        } finally {
            try {
                consumer.commitAsync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    public void processing(ConsumerRecord<String, HubEventAvro> record) {
        HubEventAvro hubEventAvro = record.value();
        if (hubEventAvro.getPayload() instanceof DeviceAddedEventAvro event) {
            sensorService.addSensor(event.getId(), hubEventAvro.getHubId());
            return;
        }
        if (hubEventAvro.getPayload() instanceof DeviceRemovedEventAvro event) {
            sensorService.removeSensor(event.getId(), hubEventAvro.getHubId());
            return;
        }
        if (hubEventAvro.getPayload() instanceof ScenarioAddedEventAvro event) {
            scenarioService.addScenario(event, hubEventAvro.getHubId());
            return;
        }
        if (hubEventAvro.getPayload() instanceof ScenarioRemovedEventAvro event) {
            scenarioService.removeScenario(event.getName());
            return;
        }
        log.warn("Не удалось определить тип события: {}", hubEventAvro.getPayload().getClass().getName());
    }
}
