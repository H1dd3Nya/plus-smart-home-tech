package ru.yandex.practicum.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.handler.HubEventHandler;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedEventHandler extends HubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaConfig kafkaConfig, KafkaEventProducer kafkaEventProducer) {
        super(kafkaConfig, kafkaEventProducer, HubEventProto.PayloadCase.DEVICE_ADDED);
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEventProto event) {
        if (event.hasDeviceAdded()) {
            DeviceAddedEventProto deviceAddedEvent = event.getDeviceAdded();
            return DeviceAddedEventAvro.newBuilder()
                    .setId(deviceAddedEvent.getId())
                    .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getType().name()))
                    .build();
        } else {
            throw new IllegalArgumentException("Событие не содержит данных о добавленном устройстве.");
        }
    }
}
