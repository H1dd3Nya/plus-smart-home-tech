package ru.yandex.practicum.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.enums.DeviceType;
import ru.yandex.practicum.enums.Operation;

@Getter
@Setter
@ToString
public class ScenarioCondition {

    private String sensorId;
    private DeviceType type;
    private Operation operation;
    private int value;
}