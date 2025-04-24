package ru.yandex.practicum.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.order.model.Order;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderDto toOrderDto(Order order);

    List<OrderDto> toOrdersDto(List<Order> orders);
}
