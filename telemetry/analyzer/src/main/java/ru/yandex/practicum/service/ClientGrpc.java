package ru.yandex.practicum.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.ScenarioAction;

@Service
public class ClientGrpc {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public ClientGrpc(@GrpcClient("hub-router")
                      HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void send(Action action, String hubId) {
        DeviceActionRequest actionRequest = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(action.getScenarioActions().stream()
                                .map(ScenarioAction::getSensor)
                                .findFirst().get().getId())
                        .setType(ActionTypeProto.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .build();
        hubRouterClient.handleDeviceAction(actionRequest);
    }
}
