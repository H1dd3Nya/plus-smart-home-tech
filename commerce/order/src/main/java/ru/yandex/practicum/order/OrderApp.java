package ru.yandex.practicum.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.interactionapi.feign.DeliveryClient;
import ru.yandex.practicum.interactionapi.feign.PaymentClient;
import ru.yandex.practicum.interactionapi.feign.ShoppingCartClient;
import ru.yandex.practicum.interactionapi.feign.WarehouseClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {WarehouseClient.class, DeliveryClient.class, PaymentClient.class, ShoppingCartClient.class})
public class OrderApp {

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }

}
