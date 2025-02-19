package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.CollectorService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/events")
public class CollectorController {
    private final CollectorService service;

    @PostMapping("/sensors")
    public void processingSensors(@Valid @RequestBody SensorEvent event) {
        service.processSensors(event);
    }

    @PostMapping("/hubs")
    public void processingHubs(@Valid @RequestBody HubEvent event) {
        service.processHub(event);
    }
}
