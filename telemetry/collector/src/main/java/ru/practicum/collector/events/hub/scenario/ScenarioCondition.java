package ru.practicum.collector.events.hub.scenario;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.collector.enums.hub.ConditionOperation;
import ru.practicum.collector.enums.hub.ConditionType;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ScenarioCondition {
    String sensorId;
    ConditionType type;
    ConditionOperation operation;
    Integer value;
}
