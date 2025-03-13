package ru.yandex.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.models.Action;
import ru.yandex.practicum.models.Condition;
import ru.yandex.practicum.models.ConditionOperation;
import ru.yandex.practicum.models.Scenario;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzerService {
    private final ScenarioService scenarioService;
    private final HubRouterGrpcClient hubRouterGrpcClient;

    public void processing(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioService.getScenariosByHubId(hubId);

        for (Scenario scenario : scenarios) {
            if (isScenarioTriggered(scenario, snapshot)) {
                executeActions(scenario.getActions(), hubId);
            }
        }
    }

    private boolean isScenarioTriggered(Scenario scenario, SensorsSnapshotAvro snapshot) {
        for (Condition condition : scenario.getConditions()) {
            if (!checkCondition(condition, snapshot)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCondition(Condition condition, SensorsSnapshotAvro snapshot) {
        SensorStateAvro sensorState = snapshot.getSensorsState().get(condition.getSensorId());

        if (sensorState == null) {
            log.warn("Нет данных для датчика: {}", condition.getSensorId());
            return false;
        }

        try {
            switch (condition.getType()) {
                case TEMPERATURE:
                    if (sensorState.getData() instanceof TemperatureSensorAvro tempSensor) {
                        return operation(tempSensor.getTemperatureC(), condition.getOperation(), condition.getValue());
                    }
                    break;
                case HUMIDITY:
                    if (sensorState.getData() instanceof ClimateSensorAvro climateSensor) {
                        return operation(climateSensor.getHumidity(), condition.getOperation(), condition.getValue());
                    }
                    break;
                case CO2LEVEL:
                    if (sensorState.getData() instanceof ClimateSensorAvro climateSensor) {
                        return operation(climateSensor.getCo2Level(), condition.getOperation(), condition.getValue());
                    }
                    break;
                case LUMINOSITY:
                    if (sensorState.getData() instanceof LightSensorAvro lightSensor) {
                        return operation(lightSensor.getLuminosity(), condition.getOperation(), condition.getValue());
                    }
                    break;
                case MOTION:
                    if (sensorState.getData() instanceof MotionSensorAvro motionSensor) {
                        int motionValue = motionSensor.getMotion() ? 1 : 0;
                        return operation(motionValue, condition.getOperation(), condition.getValue());
                    }
                    break;
                case SWITCH:
                    if (sensorState.getData() instanceof SwitchSensorAvro switchSensor) {
                        int switchState = switchSensor.getState() ? 1 : 0;
                        return operation(switchState, condition.getOperation(), condition.getValue());
                    }
                    break;
                default:
                    log.warn("Не определен тип условия: {}", condition.getType());
                    return false;
            }
        } catch (Exception e) {
            log.error("При проверки условия возникла ошибка {}: {}", condition, e.getMessage());
        }
        return false;
    }

    private boolean operation(int sensorValue, ConditionOperation operation, int targetValue) {
        return switch (operation) {
            case EQUALS -> sensorValue == targetValue;
            case GREATER_THAN -> sensorValue > targetValue;
            case LOWER_THAN -> sensorValue < targetValue;
        };
    }

    private void executeActions(List<Action> actions, String hubId) {
        for (Action action : actions) {

            DeviceActionRequest deviceActionRequest = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(action.getScenario().getName())
                    .setAction(DeviceActionProto.newBuilder()
                            .setSensorId(action.getSensorId())
                            .setType(ActionTypeProto.valueOf(action.getType().name()))
                            .setValue(action.getValue())
                            .build())
                    .build();
            hubRouterGrpcClient.send(deviceActionRequest);
            log.info("Запуск действия {}", action);
        }
    }
}
