package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.enums.HubEventType;

import java.util.List;

import static ru.yandex.practicum.enums.HubEventType.SCENARIO_ADDED;

@Getter
@Setter
@ToString
public class ScenarioAddedEvent extends ScenarioEvent {


    @NotNull
    @Size(min = 1)
    private List<DeviceAction> actions;
    @NotNull
    @Size(min = 1)
    private List<ScenarioCondition> conditions;

    public HubEventType getType() {
        return SCENARIO_ADDED;
    }
}