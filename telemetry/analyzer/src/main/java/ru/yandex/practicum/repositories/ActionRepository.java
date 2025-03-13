package ru.yandex.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.models.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
