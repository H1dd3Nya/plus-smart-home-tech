package ru.yandex.practicum.collector.handlers.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class DeviceAddedHandlerProto extends BaseHubEventHandlerProto {
    public DeviceAddedHandlerProto(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public HubEventAvro toAvro(HubEventProto hubEvent) {
        DeviceAddedEventProto deviceAddedEvent = hubEvent.getDeviceAdded();

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(mapTimestampToInstant(hubEvent))
                .setPayload(new DeviceAddedEventAvro(deviceAddedEvent.getId(),
                        mapToDeviceTypeAvro(deviceAddedEvent.getType())))
                .build();
    }

    private DeviceTypeAvro mapToDeviceTypeAvro(DeviceTypeProto deviceTypeProto) {
        return switch (deviceTypeProto) {
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case UNRECOGNIZED -> null;
        };
    }
}
