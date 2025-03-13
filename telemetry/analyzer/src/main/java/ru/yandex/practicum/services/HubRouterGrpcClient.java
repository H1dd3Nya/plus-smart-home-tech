package ru.yandex.practicum.services;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Slf4j
@Service
public class HubRouterGrpcClient {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub client;

    public HubRouterGrpcClient(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.client = hubRouterClient;
    }

    public void send(DeviceActionRequest deviceActionRequest) {
        var result = client.handleDeviceAction(deviceActionRequest);
        if (result == null) {
            log.error("Не удалось отправить сообщение устройству");
        }
    }
}
