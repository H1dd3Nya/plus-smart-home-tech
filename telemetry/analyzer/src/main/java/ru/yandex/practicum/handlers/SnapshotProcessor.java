package ru.yandex.practicum.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.services.AnalyzerService;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private static final Duration CONSUMER_TIMEOUT = Duration.ofMillis(1000);
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final AnalyzerService service;
    private final List<String> topics = Collections.singletonList("telemetry.snapshots.v1");

    public SnapshotProcessor(KafkaConfig kafkaConfig, AnalyzerService service) {
        this.service = service;
        this.consumer = new KafkaConsumer<>(kafkaConfig.getHubConsumerProperties());
    }

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(topics);
            log.info("Слушаем топик со снапшотами");
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(CONSUMER_TIMEOUT);
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
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
            log.error("Ошибка во время обработки получения снапшотов", e);
        } finally {
            try {
                consumer.commitAsync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    public void processing(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        service.processing(record.value());
    }
}
