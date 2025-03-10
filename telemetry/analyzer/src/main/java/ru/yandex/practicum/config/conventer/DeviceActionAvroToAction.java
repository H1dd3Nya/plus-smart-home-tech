package ru.yandex.practicum.config.conventer;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.type.ActionType;

@Component
public class DeviceActionAvroToAction implements Converter<DeviceActionAvro, Action> {
    @Override
    public Action convert(DeviceActionAvro source) {
        return Action.builder()
                .value(source.getValue())
                .type(ActionType.valueOf(source.getType().name()))
                .build();
    }
}
