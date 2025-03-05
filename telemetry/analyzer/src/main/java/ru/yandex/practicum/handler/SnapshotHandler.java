package ru.yandex.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.model.type.ConditionOperation;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.ClientGrpc;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotHandler {
    private final ScenarioRepository scenarioRepository;
    private final ClientGrpc clientGrpc;

    public void handle(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("Processing snapshot for hubId: {}", hubId);
        scenarioRepository.findByHubId(hubId).stream()
                .filter(s -> checkScenario(s, snapshot))
                .forEach(s -> send(s.getScenarioActions().stream()
                        .map(ScenarioAction::getAction)
                        .toList(), hubId));
    }

    private boolean checkScenario(Scenario scenario, SensorsSnapshotAvro snapshot) {
        for (ScenarioCondition scenarioCondition : scenario.getScenarioCondition()) {
            if (!checkCondition(scenarioCondition.getCondition(), snapshot)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCondition(Condition condition, SensorsSnapshotAvro snapshot) {
        ScenarioCondition scenarioCondition = condition.getScenarioConditions().getFirst();
        SensorStateAvro sensorState = snapshot.getSensorsState()
                .get(scenarioCondition.getSensor().getId());

        if (sensorState == null) {
            return false;
        }

        return switch (condition.getType()) {
            case TEMPERATURE -> getCondition(((TemperatureSensorAvro) sensorState.getData()).getTemperatureC(),
                    condition.getOperation(), condition.getValue());
            case HUMIDITY -> getCondition(((ClimateSensorAvro) sensorState.getData()).getHumidity(),
                    condition.getOperation(), condition.getValue());
            case CO2LEVEL -> getCondition(((ClimateSensorAvro) sensorState.getData()).getCo2Level(),
                    condition.getOperation(), condition.getValue());
            case LUMINOSITY -> getCondition(((LightSensorAvro) sensorState.getData()).getLuminosity(),
                    condition.getOperation(), condition.getValue());
            case MOTION -> getCondition(((MotionSensorAvro) sensorState.getData()).getMotion() ? 1 : 0,
                    condition.getOperation(), condition.getValue());
            case SWITCH -> getCondition(((SwitchSensorAvro) sensorState.getData()).getState() ? 1 : 0,
                    condition.getOperation(), condition.getValue());
        };
    }

    private boolean getCondition(int value, ConditionOperation operation, int targetValue) {
        return switch (operation) {
            case EQUALS -> value == targetValue;
            case GREATER_THAN -> value > targetValue;
            case LOWER_THAN -> value < targetValue;
        };
    }

    private void send(List<Action> actions, String hubId) {
        actions.forEach(action -> clientGrpc.send(action, hubId));
    }
}