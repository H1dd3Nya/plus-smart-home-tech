package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.model.type.ActionType;

import java.util.List;

@Entity
@Table(name = "actions")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActionType type;

    private Integer value;

    @OneToMany(mappedBy = "action")
    @ToString.Exclude
    private List<ScenarioAction> scenarioActions;
}