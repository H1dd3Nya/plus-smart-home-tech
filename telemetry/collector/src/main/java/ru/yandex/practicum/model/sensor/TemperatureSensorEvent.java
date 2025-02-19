package ru.yandex.practicum.model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.enums.SensorEventType;

import static ru.yandex.practicum.enums.SensorEventType.TEMPERATURE_SENSOR_EVENT;

@Getter
@Setter
@ToString
public class TemperatureSensorEvent extends SensorEvent {

    private int temperatureC;
    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return TEMPERATURE_SENSOR_EVENT;
    }
}