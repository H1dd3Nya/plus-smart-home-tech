package ru.yandex.practicum.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;

@RequiredArgsConstructor
public abstract class HubEventHandler<T extends SpecificRecordBase> {

    private static final String HUB_TOPIC = "telemetry.hubs.v1";
    private final KafkaConfig kafkaConfig;
    private final KafkaEventProducer kafkaEventProducer;
    private final HubEventProto.PayloadCase eventType;

    public void handle(HubEventProto event) {
        T avroEvent = mapToAvro(event);
        sendToKafka(avroEvent, HUB_TOPIC, event.getHubId(), System.currentTimeMillis());
    }

    protected abstract T mapToAvro(HubEventProto event);

    private void sendToKafka(T event, String topic, String hubId, long timestamp) {
        kafkaEventProducer.sendToKafka(event, topic, hubId, timestamp);
    }

    public HubEventProto.PayloadCase getEventType() {
        return eventType;
    }
}