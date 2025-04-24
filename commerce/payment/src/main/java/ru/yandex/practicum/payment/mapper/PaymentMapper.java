package ru.yandex.practicum.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.interactionapi.dto.PaymentDto;
import ru.yandex.practicum.payment.model.Payment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    PaymentDto toPaymentDto(Payment payment);
}
