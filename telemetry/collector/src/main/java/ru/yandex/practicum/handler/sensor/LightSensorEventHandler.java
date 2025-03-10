package ru.yandex.practicum.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler extends SensorEventHandler<LightSensorAvro> {

    public LightSensorEventHandler(KafkaConfig kafkaConfig, KafkaEventProducer kafkaEventProducer) {
        super(kafkaConfig, kafkaEventProducer, SensorEventProto.PayloadCase.LIGHT_SENSOR);
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEventProto event) {
        if (event.hasLightSensor()) {
            LightSensorProto lightSensor = event.getLightSensor();
            return LightSensorAvro.newBuilder()
                    .setLinkQuality(lightSensor.getLinkQuality())
                    .setLuminosity(lightSensor.getLuminosity())
                    .build();
        } else {
            throw new IllegalArgumentException("Событие не содержит данных о световом датчике.");
        }
    }
}
