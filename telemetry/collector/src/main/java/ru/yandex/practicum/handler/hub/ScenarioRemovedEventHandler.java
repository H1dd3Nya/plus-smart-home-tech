package ru.yandex.practicum.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.handler.HubEventHandler;
import ru.yandex.practicum.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedEventHandler extends HubEventHandler<ScenarioRemovedEventAvro> {

    public ScenarioRemovedEventHandler(KafkaConfig kafkaConfig, KafkaEventProducer kafkaEventProducer) {
        super(kafkaConfig, kafkaEventProducer, HubEventProto.PayloadCase.SCENARIO_REMOVED);
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(HubEventProto event) {
        if (event.hasScenarioRemoved()) {
            ScenarioRemovedEventProto scenarioRemovedEvent = event.getScenarioRemoved();
            return ScenarioRemovedEventAvro.newBuilder()
                    .setName(scenarioRemovedEvent.getName())
                    .build();
        } else {
            throw new IllegalArgumentException("Event does not contain scenario removed data");
        }
    }
}