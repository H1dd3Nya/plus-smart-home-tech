package ru.yandex.practicum.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorEventHandler extends SensorEventHandler<MotionSensorAvro> {

    public MotionSensorEventHandler(KafkaConfig kafkaConfig, KafkaEventProducer kafkaEventProducer) {
        super(kafkaConfig, kafkaEventProducer, SensorEventProto.PayloadCase.MOTION_SENSOR);
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEventProto event) {
        if (event.hasMotionSensor()) {
            MotionSensorProto motionSensor = event.getMotionSensor();
            return MotionSensorAvro.newBuilder()
                    .setLinkQuality(motionSensor.getLinkQuality())
                    .setMotion(motionSensor.getMotion())
                    .setVoltage(motionSensor.getVoltage())
                    .build();
        } else {
            throw new IllegalArgumentException("Событие не содержит данных о датчике движения.");
        }
    }
}
