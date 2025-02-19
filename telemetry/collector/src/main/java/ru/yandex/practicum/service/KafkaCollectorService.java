package ru.yandex.practicum.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.*;
import ru.yandex.practicum.serialize.AvroSerializer;

import java.math.BigInteger;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaCollectorService implements CollectorService {

    public static final String TELEMETRY_SENSORS_V1 = "telemetry.sensors.v1";
    public static final String TELEMETRY_HUBS_V1 = "telemetry.hubs.v1";

    private Producer<String, SpecificRecordBase> producer;

    public void processSensors(SensorEvent sensorEvent) {
        SpecificRecordBase event = mapToAvro(sensorEvent);
        String hubId = sensorEvent.getHubId();
        long timestamp = sensorEvent.getTimestamp().toEpochMilli();
        sendToKafka(event, TELEMETRY_SENSORS_V1, hubId, timestamp);
    }

    public void processHub(HubEvent hubEvent) {
        SpecificRecordBase event = mapToAvro(hubEvent);
        String hubId = hubEvent.getHubId();
        long timestamp = hubEvent.getTimestamp().toEpochMilli();
        sendToKafka(event, TELEMETRY_HUBS_V1, hubId, timestamp);
    }

    private <T extends SpecificRecordBase> void sendToKafka(T event, String topic, String hubId, long timestamp) {
        ensureProducerInitialized();

        Header header = new RecordHeader("timestamp", BigInteger.valueOf(timestamp).toByteArray());
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, hubId, event, List.of(header));

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке сообщения в Kafka: {}", exception.getMessage());
            } else {
                log.info("Сообщение отправлено в Kafka: topic={}, partition={}, offset={}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    private void ensureProducerInitialized() {
        if (producer == null) {
            producer = new KafkaProducer<>(getPropertiesSensor());
        }
    }

    private Properties getPropertiesSensor() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
        return config;
    }

    private SpecificRecordBase mapToAvro(SensorEvent sensorEvent) {
        switch (sensorEvent.getType()) {
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent climateEvent = (ClimateSensorEvent) sensorEvent;
                return new ClimateSensorAvro(
                        climateEvent.getTemperatureC(),
                        climateEvent.getHumidity(),
                        climateEvent.getCo2Level()
                );
            }
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent lightEvent = (LightSensorEvent) sensorEvent;
                return new LightSensorAvro(
                        lightEvent.getLinkQuality(),
                        lightEvent.getLuminosity()
                );
            }
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent motionEvent = (MotionSensorEvent) sensorEvent;
                return new MotionSensorAvro(
                        motionEvent.getLinkQuality(),
                        motionEvent.isMotion(),
                        motionEvent.getVoltage()
                );
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent switchEvent = (SwitchSensorEvent) sensorEvent;
                return new SwitchSensorAvro(switchEvent.isState());
            }
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent temperatureEvent = (TemperatureSensorEvent) sensorEvent;
                return new TemperatureSensorAvro(
                        temperatureEvent.getTemperatureC(),
                        temperatureEvent.getTemperatureF()
                );
            }
            default ->
                    throw new IllegalArgumentException("Неподдерживаемый тип события сенсора: " + sensorEvent.getType());
        }
    }

    private SpecificRecordBase mapToAvro(HubEvent hubEvent) {
        switch (hubEvent.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent addedEvent = (DeviceAddedEvent) hubEvent;
                return new DeviceAddedEventAvro(
                        addedEvent.getId(),
                        DeviceTypeAvro.valueOf(addedEvent.getDeviceType().name())
                );
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEvent removedEvent = (DeviceRemovedEvent) hubEvent;
                return new DeviceRemovedEventAvro(removedEvent.getId());
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;
                List<ScenarioConditionAvro> conditionAvros = scenarioAddedEvent.getConditions().stream()
                        .map(sc -> new ScenarioConditionAvro(
                                sc.getSensorId(),
                                DeviceTypeAvro.valueOf(sc.getType().name()),
                                ConditionOperationAvro.valueOf(sc.getOperation().name()),
                                sc.getValue()))
                        .collect(Collectors.toList());

                List<DeviceActionAvro> actionAvros = scenarioAddedEvent.getActions().stream()
                        .map(da -> new DeviceActionAvro(
                                da.getSensorId(),
                                ActionTypeAvro.valueOf(da.getType().name()),
                                da.getValue()))
                        .collect(Collectors.toList());

                return new ScenarioAddedEventAvro(
                        scenarioAddedEvent.getName(),
                        conditionAvros,
                        actionAvros
                );
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) hubEvent;
                return new ScenarioRemovedEventAvro(scenarioRemovedEvent.getName());
            }
            default -> throw new IllegalArgumentException("Неподдерживаемый тип события хаба: " + hubEvent.getType());
        }
    }

    @PreDestroy
    public void closeProducer() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}