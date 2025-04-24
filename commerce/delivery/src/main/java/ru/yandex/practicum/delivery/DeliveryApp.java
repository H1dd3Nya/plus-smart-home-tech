package ru.yandex.practicum.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.interactionapi.feign.OrderClient;
import ru.yandex.practicum.interactionapi.feign.WarehouseClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {WarehouseClient.class, OrderClient.class})
public class DeliveryApp {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryApp.class, args);
    }

}
