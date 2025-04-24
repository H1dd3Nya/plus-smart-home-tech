package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interactionapi.dto.AddressDto;
import ru.yandex.practicum.interactionapi.dto.DeliveryDto;
import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.interactionapi.enums.DeliveryState;
import ru.yandex.practicum.interactionapi.feign.OrderClient;
import ru.yandex.practicum.interactionapi.feign.WarehouseClient;
import ru.yandex.practicum.interactionapi.request.ShippedToDeliveryRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private static final double BASERATE = 5.0;
    private static final String ADDRESS1 = "ADDRESS_1";
    private static final String ADDRESS2 = "ADDRESS_2";
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toDelivery(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return deliveryMapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NoDeliveryFoundException("Не найдена доставка"));
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.complete(delivery.getOrderId());
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NoDeliveryFoundException("Не найдена доставка для выдачи"));
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        orderClient.assembly(delivery.getOrderId());
        ShippedToDeliveryRequest deliveryRequest = new ShippedToDeliveryRequest(
                delivery.getOrderId(), delivery.getDeliveryId());
        warehouseClient.shippedToDelivery(deliveryRequest);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NoDeliveryFoundException("Не найдена доставка для сбоя"));
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.assemblyFailed(delivery.getOrderId());
    }

    @Override
    @Transactional(readOnly = true)
    public Double deliveryCost(OrderDto orderDto) {
        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId()).orElseThrow(
                () -> new NoDeliveryFoundException("Не найдена доставка для расчёта"));

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        double addressCost = switch (warehouseAddress.getCity()) {
            case ADDRESS1 -> BASERATE * 1;
            case ADDRESS2 -> BASERATE * 2;
            default ->
                    throw new IllegalStateException(String.format("Unexpected value: %s", warehouseAddress.getCity()));
        };
        double deliveryCost = BASERATE + addressCost;
        if (orderDto.getFragile()) deliveryCost += deliveryCost * 0.2;
        deliveryCost += orderDto.getDeliveryWeight() * 0.3;
        deliveryCost += orderDto.getDeliveryVolume() * 0.2;
        if (!warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())) {
            deliveryCost += deliveryCost * 0.2;
        }
        return deliveryCost;
    }
}
