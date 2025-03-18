package ru.yandex.practicum.handlers.event.added;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handlers.event.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.ScenarioEventMapper;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;
    private final ScenarioEventMapper mapper;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();

        Optional<Scenario> savedScenario = scenarioRepository.findByHubIdAndName(event.getHubId(),
                scenarioAddedEvent.getName());

        Scenario scenario;
        if (savedScenario.isEmpty()) {
            scenario = scenarioRepository.save(mapper.mapToScenario(event));
        } else {
            scenario = savedScenario.get();
        }

        if (isSensorsInScenarioConditions(scenarioAddedEvent, event.getHubId())) {
            conditionRepository.saveAll(mapper.mapToCondition(scenarioAddedEvent, scenario));
        }
        if (isSensorsInScenarioActions(scenarioAddedEvent, event.getHubId())) {
            actionRepository.saveAll(mapper.mapToAction(scenarioAddedEvent, scenario));
        }
    }

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    private boolean isSensorsInScenarioConditions(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsByIdInAndHubId(scenarioAddedEvent.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList(), hubId);
    }

    private boolean isSensorsInScenarioActions(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsByIdInAndHubId(scenarioAddedEvent.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .toList(), hubId);
    }
}
