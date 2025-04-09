package ru.yandex.practicum.collector.events.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.collector.enums.sensor.SensorEventType;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class TemperatureSensorEvent extends SensorEvent {
    @NotNull
    Integer temperatureC;
    @NotNull
    Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
