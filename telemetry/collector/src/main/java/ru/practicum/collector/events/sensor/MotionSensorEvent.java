package ru.practicum.collector.events.sensor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.collector.enums.sensor.SensorEventType;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class MotionSensorEvent extends SensorEvent {
    @NotNull
    Integer linkQuality;
    @NotNull
    Boolean motion;
    @NotNull
    @PositiveOrZero
    Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
