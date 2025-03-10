package ru.yandex.practicum.config.conventer;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.type.ConditionOperation;
import ru.yandex.practicum.model.type.ConditionType;

@Component
public class ScenarioConditionAvroToConditionConverter
        implements Converter<ScenarioConditionAvro, Condition> {

    @Override
    public Condition convert(ScenarioConditionAvro source) {
        return Condition.builder()
                .operation(ConditionOperation.valueOf(source.getOperation().name()))
                .type(ConditionType.valueOf(source.getType().name()))
                .value(convertToValue(source.getValue()))
                .build();
    }

    private Integer convertToValue(Object value) {
        if (value instanceof Integer) return (Integer) value;
        else if (value instanceof Boolean) return (Boolean) value ? 1 : 0;
        else return null;
    }
}
