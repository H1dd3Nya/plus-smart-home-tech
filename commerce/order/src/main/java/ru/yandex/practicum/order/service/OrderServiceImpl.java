package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.interactionapi.dto.DeliveryDto;
import ru.yandex.practicum.interactionapi.dto.OrderDto;
import ru.yandex.practicum.interactionapi.dto.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.enums.OrderState;
import ru.yandex.practicum.interactionapi.exception.NotAuthorizedUserException;
import ru.yandex.practicum.interactionapi.feign.DeliveryClient;
import ru.yandex.practicum.interactionapi.feign.PaymentClient;
import ru.yandex.practicum.interactionapi.feign.ShoppingCartClient;
import ru.yandex.practicum.interactionapi.feign.WarehouseClient;
import ru.yandex.practicum.interactionapi.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interactionapi.request.CreateNewOrderRequest;
import ru.yandex.practicum.interactionapi.request.ProductReturnRequest;
import ru.yandex.practicum.order.exception.NoOrderFoundException;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartClient shoppingCartClient;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    private final String MESSAGE_ORDER_NOT_FOUND = "Заказ не найден.";

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username, Integer page, Integer size) {
        if (StringUtils.isEmpty(username))
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым.");
        ShoppingCartDto shoppingCart = shoppingCartClient.getShoppingCart(username);

        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(page, size, sortByCreated);
        List<Order> orders = orderRepository.findByShoppingCartId(shoppingCart.getShoppingCartId(), pageRequest);
        return orderMapper.toOrdersDto(orders);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest newOrderRequest) {
        Order order = Order.builder()
                .shoppingCartId(newOrderRequest.getShoppingCart().getShoppingCartId())
                .products(newOrderRequest.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();
        Order newOrder = orderRepository.save(order);

        BookedProductsDto bookedProducts = warehouseClient.assemblyProductForOrder(
                new AssemblyProductsForOrderRequest(
                        newOrderRequest.getShoppingCart().getShoppingCartId(),
                        newOrder.getOrderId()
                ));

        newOrder.setFragile(bookedProducts.getFragile());
        newOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        newOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        newOrder.setProductPrice(paymentClient.productCost(orderMapper.toOrderDto(newOrder)));

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(newOrder.getOrderId())
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(newOrderRequest.getDeliveryAddress())
                .build();
        newOrder.setDeliveryId(deliveryClient.planDelivery(deliveryDto).getDeliveryId());

        paymentClient.createPayment(orderMapper.toOrderDto(newOrder));
        return orderMapper.toOrderDto(newOrder);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest returnRequest) {
        Order order = orderRepository.findById(returnRequest.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        warehouseClient.acceptReturn(returnRequest.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        order.setState(OrderState.PAID);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        order.setState(OrderState.DELIVERED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        order.setState(OrderState.COMPLETED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        order.setTotalPrice(paymentClient.getTotalCost(orderMapper.toOrderDto(order)));
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        order.setDeliveryPrice(deliveryClient.deliveryCost(orderMapper.toOrderDto(order)));
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        order.setState(OrderState.ASSEMBLED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(MESSAGE_ORDER_NOT_FOUND));
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toOrderDto(order);
    }
}
