package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.model.type.ConditionOperation;
import ru.yandex.practicum.model.type.ConditionType;

import java.util.List;

@Entity
@Table(name = "conditions")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConditionType type;

    @Enumerated(EnumType.STRING)
    private ConditionOperation operation;

    private Integer value;

    @OneToMany(mappedBy = "condition", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<ScenarioCondition> scenarioConditions;
}
