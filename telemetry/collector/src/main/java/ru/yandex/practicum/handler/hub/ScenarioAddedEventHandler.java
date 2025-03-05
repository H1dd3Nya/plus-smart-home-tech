package ru.yandex.practicum.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.handler.HubEventHandler;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioAddedEventHandler extends HubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaConfig kafkaConfig, KafkaEventProducer kafkaEventProducer) {
        super(kafkaConfig, kafkaEventProducer, HubEventProto.PayloadCase.SCENARIO_ADDED);
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        if (event.hasScenarioAdded()) {
            ScenarioAddedEventProto scenarioAddedEvent = event.getScenarioAdded();
            return ScenarioAddedEventAvro.newBuilder()
                    .setName(scenarioAddedEvent.getName())
                    .setConditions(mapConditions(scenarioAddedEvent.getConditionList()))
                    .setActions(mapActions(scenarioAddedEvent.getActionList()))
                    .build();
        } else {
            throw new IllegalArgumentException("Event does not contain scenario added data");
        }
    }

    private List<ScenarioConditionAvro> mapConditions(List<ScenarioConditionProto> conditions) {
        return conditions.stream()
                .map(cond -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(cond.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(cond.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(cond.getOperation().name()))
                        .setValue(cond.hasBoolValue() ? cond.getBoolValue() : cond.getIntValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<DeviceActionAvro> mapActions(List<DeviceActionProto> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .setValue(action.hasValue() ? action.getValue() : null)
                        .build())
                .collect(Collectors.toList());
    }
}