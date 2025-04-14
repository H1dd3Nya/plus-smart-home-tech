package ru.yandex.practicum.analyzer.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.handlers.event.HubEventHandler;
import ru.yandex.practicum.analyzer.handlers.event.HubEventHandlers;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Consumer<String, HubEventAvro> consumer;
    private final HubEventHandlers handlers;
    @Value("${topic.hub-event-topic}")
    private String topic;
    @Value("${spring.kafka.consumer.hub-processor-poll-timeout}")
    private int pollTimeout;

    @Override
    public void run() {

        try {
            consumer.subscribe(List.of(topic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            Map<String, HubEventHandler> handlerMap = handlers.getHandlers();

            while (true) {

                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    String payloadName = event.getPayload().getClass().getSimpleName();
                    log.info("Recieved hub message of type: {}", payloadName);

                    if (handlerMap.containsKey(payloadName)) {
                        handlerMap.get(payloadName).handle(event);
                    } else {
                        throw new IllegalArgumentException("No available handler found " + event);
                    }
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Error reading data from topic: {}", topic);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
