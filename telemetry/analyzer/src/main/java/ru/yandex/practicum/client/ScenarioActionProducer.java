package ru.yandex.practicum.client;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.mapper.ActionMapper;
import ru.yandex.practicum.model.Action;

@Slf4j
@Service
public class ScenarioActionProducer {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;
    private final ActionMapper mapper;

    public ScenarioActionProducer(
            @GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub) {
        this.hubRouterStub = hubRouterStub;
        this.mapper = new ActionMapper();
    }

    public void sendAction(Action action) {
        DeviceActionRequest actionRequest = mapper.mapToActionRequest(action);

        hubRouterStub.handleDeviceAction(actionRequest);
        log.info("Action {} send to hub-router", actionRequest);
    }
}
