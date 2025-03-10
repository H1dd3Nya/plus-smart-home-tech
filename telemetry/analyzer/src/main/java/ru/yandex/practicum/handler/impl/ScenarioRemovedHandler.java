package ru.yandex.practicum.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handler.HubHandlerEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.repository.ScenarioRepository;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements HubHandlerEvent {
    private final ScenarioRepository repository;

    @Override
    public String getType() {
        return ScenarioRemovedEventAvro.class.getName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioRemovedEventAvro removedEvent = (ScenarioRemovedEventAvro) event.getPayload();
        repository.findByHubIdAndName(event.getHubId(), removedEvent.getName())
                .ifPresent(repository::delete);
    }
}
