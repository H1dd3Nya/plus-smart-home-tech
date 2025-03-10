package ru.practicum.collector.handlers.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class MotionSensorHandlerProto extends BaseSensorHandlerProto {
    public MotionSensorHandlerProto(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public SensorEventAvro toAvro(SensorEventProto sensorEvent) {
        MotionSensorProto motionSensor = sensorEvent.getMotionSensor();

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(mapTimestampToInstant(sensorEvent))
                .setPayload(MotionSensorAvro.newBuilder()
                        .setMotion(motionSensor.getMotion())
                        .setLinkQuality(motionSensor.getLinkQuality())
                        .setVoltage(motionSensor.getVoltage())
                        .build())
                .build();
    }
}
