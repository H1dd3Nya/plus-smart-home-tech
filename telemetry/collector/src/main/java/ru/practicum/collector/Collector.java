package ru.practicum.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
<<<<<<<< HEAD:infra/config-server/src/main/java/ru/yandex/practicum/ConfigServer.java
public class ConfigServer {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServer.class, args);
========
public class Collector {
    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
>>>>>>>> 4618ef2 (Added analyzer):telemetry/collector/src/main/java/ru/practicum/collector/Collector.java
    }

}