package ru.practicum.collector.events.hub.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.collector.enums.hub.DeviceType;
import ru.practicum.collector.enums.hub.HubEventType;
import ru.practicum.collector.events.hub.HubEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    String id;
    @NotNull
    DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
