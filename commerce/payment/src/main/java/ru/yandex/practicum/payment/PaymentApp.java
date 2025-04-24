package ru.yandex.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.interactionapi.feign.OrderClient;
import ru.yandex.practicum.interactionapi.feign.ShoppingStoreClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {ShoppingStoreClient.class, OrderClient.class})
public class PaymentApp {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }

}
