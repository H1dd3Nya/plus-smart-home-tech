package ru.yandex.practicum.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handler.HubHandlerEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.model.type.ScenarioActionId;
import ru.yandex.practicum.model.type.ScenarioConditionId;
import ru.yandex.practicum.repository.*;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubHandlerEvent {
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConversionService conversionService;
    private final ConditionRepository conditionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro addedEvent = (ScenarioAddedEventAvro) event.getPayload();
        Scenario scenario = Scenario.builder()
                .hubId(event.getHubId())
                .name(addedEvent.getName())
                .build();
        scenarioRepository.save(scenario);

        List<Condition> conditions = new ArrayList<>();
        List<ScenarioCondition> scenarioConditions = new ArrayList<>();

        addedEvent.getConditions().forEach(scenarioConditionAvro -> {
            Condition condition = conversionService.convert(scenarioConditionAvro, Condition.class);
            assert condition != null;
            conditions.add(condition);
            ScenarioCondition scenarioCondition = ScenarioCondition.builder()
                    .id(new ScenarioConditionId())
                    .scenario(scenario)
                    .sensor(sensorRepository.findById(scenarioConditionAvro.getSensorId()).orElseThrow())
                    .condition(condition)
                    .build();
            scenarioConditions.add(scenarioCondition);
        });
        conditionRepository.saveAll(conditions);
        scenarioConditionRepository.saveAll(scenarioConditions);

        List<Action> actions = new ArrayList<>();
        List<ScenarioAction> scenarioActions = new ArrayList<>();

        addedEvent.getActions().forEach(deviceActionAvro -> {
            Action action = conversionService.convert(deviceActionAvro, Action.class);
            assert action != null;
            actions.add(action);
            ScenarioAction scenarioAction = ScenarioAction.builder()
                    .id(new ScenarioActionId())
                    .action(action)
                    .sensor(sensorRepository.findById(deviceActionAvro.getSensorId()).orElseThrow())
                    .scenario(scenario)
                    .build();
            scenarioActions.add(scenarioAction);
        });

        actionRepository.saveAll(actions);
        scenarioActionRepository.saveAll(scenarioActions);
    }
}
