package ru.practicum.collector.handlers.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandlerProto {
    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto event);
}
