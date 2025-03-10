package ru.yandex.practicum.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorEventHandler extends SensorEventHandler<SwitchSensorAvro> {

    public SwitchSensorEventHandler(KafkaConfig kafkaConfig, KafkaEventProducer kafkaEventProducer) {
        super(kafkaConfig, kafkaEventProducer, SensorEventProto.PayloadCase.SWITCH_SENSOR);
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEventProto event) {
        if (event.hasSwitchSensor()) {
            SwitchSensorProto switchSensor = event.getSwitchSensor();
            return SwitchSensorAvro.newBuilder()
                    .setState(switchSensor.getState())
                    .build();
        } else {
            throw new IllegalArgumentException("Событие не содержит данных о датчике переключателя.");
        }
    }
}
