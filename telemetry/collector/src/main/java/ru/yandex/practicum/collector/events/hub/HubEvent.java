package ru.yandex.practicum.collector.events.hub;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.collector.enums.hub.HubEventType;
import ru.yandex.practicum.collector.events.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.collector.events.hub.device.DeviceRemovedEvent;
import ru.yandex.practicum.collector.events.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.collector.events.hub.scenario.ScenarioRemovedEvent;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = HubEventType.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public abstract class HubEvent {
    final Instant timestamp = Instant.now();
    @NotBlank
    String hubId;

    @NotNull
    public abstract HubEventType getType();
}
