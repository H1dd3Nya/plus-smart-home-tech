package ru.practicum.collector.handlers.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseHubEventHandlerProto implements HubEventHandlerProto {
    private final KafkaEventProducer producer;
    @Value("${topic.telemetry-hubs}")
    private String topic;

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro hubEvent = toAvro(event);
        log.info("Отправляем событие хаба {}", hubEvent);
        producer.send(hubEvent, event.getHubId(), mapTimestampToInstant(event), topic);
    }

    public Instant mapTimestampToInstant(HubEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }

    public abstract HubEventAvro toAvro(HubEventProto hubEvent);
}
