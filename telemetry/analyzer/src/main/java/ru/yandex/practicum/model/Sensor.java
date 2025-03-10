package ru.yandex.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "sensors")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    @Id
    private String id;

    @NotBlank
    @Column(name = "hub_id")
    private String hubId;

    @OneToMany(mappedBy = "sensor", fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<ScenarioCondition> scenarioConditions;

    @OneToMany(mappedBy = "sensor", fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<ScenarioAction> scenarioActions;
}
