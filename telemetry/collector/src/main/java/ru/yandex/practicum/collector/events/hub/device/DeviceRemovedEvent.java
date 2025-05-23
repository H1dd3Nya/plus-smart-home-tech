package ru.yandex.practicum.collector.events.hub.device;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.collector.enums.hub.HubEventType;
import ru.yandex.practicum.collector.events.hub.HubEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class DeviceRemovedEvent extends HubEvent {
    @NotBlank
    String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
