package ru.yandex.practicum.handlers.event.removed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handlers.event.HubEventHandler;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceRemovedHandler implements HubEventHandler {
    private final SensorRepository repository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro removedEvent = (DeviceRemovedEventAvro) event.getPayload();
        log.info("Removing device with id = {} from hub with hub_id = {}", removedEvent.getId(), event.getHubId());
        repository.deleteByIdAndHubId(removedEvent.getId(), event.getHubId());
    }

    @Override
    public String getPayloadType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }
}
