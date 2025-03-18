package ru.practicum.collector.events.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.collector.enums.hub.HubEventType;
import ru.practicum.collector.events.hub.HubEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ScenarioRemovedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
