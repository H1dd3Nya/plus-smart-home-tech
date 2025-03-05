package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.type.ScenarioActionId;

@Entity
@Table(name = "scenario_actions")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioAction {

    @EmbeddedId
    @Builder.Default
    private ScenarioActionId id = new ScenarioActionId();

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("actionId")
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;
}