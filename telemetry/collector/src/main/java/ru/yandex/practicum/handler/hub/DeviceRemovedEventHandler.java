package ru.yandex.practicum.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.handler.HubEventHandler;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
public class DeviceRemovedEventHandler extends HubEventHandler<DeviceRemovedEventAvro> {

    public DeviceRemovedEventHandler(KafkaConfig kafkaConfig, KafkaEventProducer kafkaEventProducer) {
        super(kafkaConfig, kafkaEventProducer, HubEventProto.PayloadCase.DEVICE_REMOVED);
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEventProto event) {
        if (event.hasDeviceRemoved()) {
            DeviceRemovedEventProto deviceRemovedEvent = event.getDeviceRemoved();
            return DeviceRemovedEventAvro.newBuilder()
                    .setId(deviceRemovedEvent.getId())
                    .build();
        } else {
            throw new IllegalArgumentException("Событие не содержит данных об удалённом устройстве.");
        }
    }
}
