package ru.practicum.collector.events.sensor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.collector.enums.sensor.SensorEventType;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class LightSensorEvent extends SensorEvent {
    @NotNull
    Integer linkQuality;
    @NotNull
    @PositiveOrZero
    Integer luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
