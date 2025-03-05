package ru.yandex.practicum.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
public class ClimateSensorEventHandler extends SensorEventHandler<ClimateSensorAvro> {

    public ClimateSensorEventHandler(KafkaConfig kafkaConfig, KafkaEventProducer kafkaEventProducer) {
        super(kafkaConfig, kafkaEventProducer, SensorEventProto.PayloadCase.CLIMATE_SENSOR);
    }

    @Override
    protected ClimateSensorAvro mapToAvro(SensorEventProto event) {
        if (event.hasClimateSensor()) {
            ClimateSensorProto climateSensor = event.getClimateSensor();
            return ClimateSensorAvro.newBuilder()
                    .setTemperatureC(climateSensor.getTemperatureC())
                    .setHumidity(climateSensor.getHumidity())
                    .setCo2Level(climateSensor.getCo2Level())
                    .build();
        } else {
            throw new IllegalArgumentException("Event does not contain climate sensor data");
        }
    }
}