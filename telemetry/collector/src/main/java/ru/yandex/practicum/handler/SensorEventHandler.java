package ru.yandex.practicum.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;

@RequiredArgsConstructor
public abstract class SensorEventHandler<T extends SpecificRecordBase> {

    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";
    private final KafkaConfig kafkaConfig;
    private final KafkaEventProducer kafkaEventProducer;
    private final SensorEventProto.PayloadCase eventType; // Тип события

    public void handle(SensorEventProto event) {
        T avroEvent = mapToAvro(event);
        sendToKafka(avroEvent, SENSOR_TOPIC, event.getHubId(), System.currentTimeMillis());
    }

    protected abstract T mapToAvro(SensorEventProto event);

    private void sendToKafka(T event, String topic, String hubId, long timestamp) {
        kafkaEventProducer.sendToKafka(event, topic, hubId, timestamp);
    }

    public SensorEventProto.PayloadCase getEventType() {
        return eventType; // Вернёт тип события
    }
}