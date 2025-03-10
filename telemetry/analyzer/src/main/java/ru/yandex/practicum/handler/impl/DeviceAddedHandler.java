package ru.yandex.practicum.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handler.HubHandlerEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceAddedHandler implements HubHandlerEvent {
    private final SensorRepository repository;

    @Override
    public String getType() {
        return DeviceAddedEventAvro.class.getName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceAddedEventAvro deviceEvent = (DeviceAddedEventAvro) event.getPayload();
        if (!repository.existsByIdInAndHubId(List.of(deviceEvent.getId()), event.getHubId())) {
            Sensor sensor = Sensor.builder()
                    .hubId(event.getHubId())
                    .id(deviceEvent.getId())
                    .build();
            repository.save(sensor);
        }
    }
}
