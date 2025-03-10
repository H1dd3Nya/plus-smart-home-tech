package ru.yandex.practicum.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorEventHandler extends SensorEventHandler<TemperatureSensorAvro> {

    public TemperatureSensorEventHandler(KafkaConfig kafkaConfig, KafkaEventProducer kafkaEventProducer) {
        super(kafkaConfig, kafkaEventProducer, SensorEventProto.PayloadCase.TEMPERATURE_SENSOR);
    }

    @Override
    protected TemperatureSensorAvro mapToAvro(SensorEventProto event) {
        if (event.hasTemperatureSensor()) {
            TemperatureSensorProto temperatureSensor = event.getTemperatureSensor();
            return TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(temperatureSensor.getTemperatureC())
                    .setTemperatureF(temperatureSensor.getTemperatureF())
                    .build();
        } else {
            throw new IllegalArgumentException("Событие не содержит данных о температурном датчике.");
        }
    }
}
