package ru.yandex.practicum.handler;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Getter
public class HubHandler {
    private final Map<String, HubHandlerEvent> context;

    public HubHandler(Set<HubHandlerEvent> handlers) {
        context = handlers.stream()
                .collect(Collectors.toMap(HubHandlerEvent::getType, handler -> handler));
    }
}