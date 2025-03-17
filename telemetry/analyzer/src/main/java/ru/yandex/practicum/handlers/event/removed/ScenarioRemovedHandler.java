package ru.yandex.practicum.handlers.event.removed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handlers.event.HubEventHandler;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioRemovedEventAvro scenarioRemovedEvent = (ScenarioRemovedEventAvro) event.getPayload();
        log.info("Removing Scenario with name = {} from hub with id = {}", scenarioRemovedEvent.getName(), event.getHubId());
        Optional<Scenario> savedScenario = scenarioRepository.findByHubIdAndName(event.getHubId(), scenarioRemovedEvent.getName());

        savedScenario.ifPresent((scenario -> {
            conditionRepository.deleteByScenario(scenario);
            actionRepository.deleteByScenario(scenario);
            scenarioRepository.delete(scenario);
        }));
    }

    @Override
    public String getPayloadType() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }
}
