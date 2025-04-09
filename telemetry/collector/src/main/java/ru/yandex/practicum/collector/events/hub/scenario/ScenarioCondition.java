package ru.yandex.practicum.collector.events.hub.scenario;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.collector.enums.hub.ConditionOperation;
import ru.yandex.practicum.collector.enums.hub.ConditionType;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ScenarioCondition {
    String sensorId;
    ConditionType type;
    ConditionOperation operation;
    Integer value;
}
