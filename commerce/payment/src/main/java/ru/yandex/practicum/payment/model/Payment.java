package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.interactionapi.enums.PaymentState;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID paymentId;
    UUID orderId;
    double productsTotal;
    double deliveryTotal;
    double totalPayment;
    double feeTotal;
    @Enumerated(EnumType.STRING)
    PaymentState status;
}
