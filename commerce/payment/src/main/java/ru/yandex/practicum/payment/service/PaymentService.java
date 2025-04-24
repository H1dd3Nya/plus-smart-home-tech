package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.interactionapi.dto.PaymentDto;

import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(OrderDto orderDto);

    Double getTotalCost(OrderDto orderDto);

    void paymentSuccess(UUID uuid);

    Double productCost(OrderDto orderDto);

    void paymentFailed(UUID uuid);
}
