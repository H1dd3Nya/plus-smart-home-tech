package ru.yandex.practicum.collector.events.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.collector.enums.hub.HubEventType;
import ru.yandex.practicum.collector.events.hub.HubEvent;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    String name;
    @NotEmpty
    List<ScenarioCondition> conditions;
    @NotEmpty
    List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
