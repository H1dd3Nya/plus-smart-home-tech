package ru.yandex.practicum.serialization;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SnapshotDeserializer extends AvroDeserializer<SensorsSnapshotAvro> {
    public SnapshotDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
