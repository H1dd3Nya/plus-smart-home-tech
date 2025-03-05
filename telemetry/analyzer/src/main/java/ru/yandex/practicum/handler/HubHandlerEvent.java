package ru.yandex.practicum.handler;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubHandlerEvent {
    String getType();

    void handle(HubEventAvro event);
}