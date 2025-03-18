package ru.practicum.collector.events.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.collector.enums.sensor.SensorEventType;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class SwitchSensorEvent extends SensorEvent {
    @NotNull
    Boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
