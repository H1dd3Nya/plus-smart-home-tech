package ru.yandex.practicum.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class SnapshotRepository {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro curSnapshot;
        if (snapshots.containsKey(event.getHubId())) {
            curSnapshot = snapshots.get(event.getHubId());
        } else {
            curSnapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(event.getHubId())
                    .setTimestamp(Instant.now())
                    .setSensorsState(new HashMap<>())
                    .build();
        }

        if (curSnapshot.getSensorsState().get(event.getId()) != null) {
            SensorStateAvro oldState = curSnapshot.getSensorsState().get(event.getId());
            if ((oldState.getTimestamp().isBefore(event.getTimestamp())
                    || oldState.getTimestamp().equals(event.getTimestamp()))
                    && oldState.getData().equals(event.getPayload()))
                return Optional.empty();
        }

        SensorStateAvro newSensorState = new SensorStateAvro(event.getTimestamp(), event.getPayload());
        curSnapshot.getSensorsState().put(event.getId(), newSensorState);
        curSnapshot.setTimestamp(event.getTimestamp());
        snapshots.put(curSnapshot.getHubId(), curSnapshot);
        return Optional.of(curSnapshot);
    }
}