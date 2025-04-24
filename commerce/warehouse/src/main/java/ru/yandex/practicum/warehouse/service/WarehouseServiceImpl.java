package ru.yandex.practicum.warehouse.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interactionapi.dto.AddressDto;
import ru.yandex.practicum.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.interactionapi.dto.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.enums.QuantityState;
import ru.yandex.practicum.interactionapi.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interactionapi.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.interactionapi.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.interactionapi.feign.ShoppingStoreClient;
import ru.yandex.practicum.interactionapi.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interactionapi.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.address.Address;
import ru.yandex.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.BookingMapper;
import ru.yandex.practicum.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.warehouse.model.Booking;
import ru.yandex.practicum.warehouse.model.Warehouse;
import ru.yandex.practicum.warehouse.repository.BookingRepository;
import ru.yandex.practicum.warehouse.repository.WarehouseRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Override
    public void addNewProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        warehouseRepository.findById(newProductInWarehouseRequest.getProductId()).ifPresent(warehouse -> {
            throw new SpecifiedProductAlreadyInWarehouseException("Ошибка, товар с таким описанием уже зарегистрирован на складе.");
        });
        Warehouse warehouse = warehouseMapper.toWarehouse(newProductInWarehouseRequest);
        warehouseRepository.save(warehouse);
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest) {
        Booking booking = bookingRepository.findByOrderId(deliveryRequest.getOrderId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе."));
        booking.setDeliveryId(deliveryRequest.getDeliveryId());
    }

    @Override
    public void acceptReturn(Map<UUID, Long> products) {
        List<Warehouse> warehousesItems = warehouseRepository.findAllById(products.keySet());
        for (Warehouse warehouse : warehousesItems) {
            warehouse.setQuantity(warehouse.getQuantity() + products.get(warehouse.getProductId()));
        }
    }

    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> products = shoppingCartDto.getProducts();
        Set<UUID> cartProductIds = products.keySet();
        Map<UUID, Warehouse> warehouseProducts = warehouseRepository.findAllById(cartProductIds)
                .stream()
                .collect(Collectors.toMap(Warehouse::getProductId, Function.identity()));

        Set<UUID> productIds = warehouseProducts.keySet();
        cartProductIds.forEach(id -> {
            if (!productIds.contains(id)) {
                throw new ProductInShoppingCartNotInWarehouse("Ошибка, товар из корзины отсутствует в БД склада.");
            }
        });
        products.forEach((key, value) -> {
            if (warehouseProducts.get(key).getQuantity() < value) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException("Ошибка, товар из корзины не находится в требуемом количестве на складе.");
            }
        });
        return getBookedProducts(warehouseProducts.values(), products);
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrder) {
        Booking booking = bookingRepository.findById(assemblyProductsForOrder.getShoppingCartId()).orElseThrow(
                () -> new RuntimeException(String.format("Shopping cart %s not found", assemblyProductsForOrder.getShoppingCartId()))
        );

        Map<UUID, Long> productsInBooking = booking.getProducts();
        List<Warehouse> productsInWarehouse = warehouseRepository.findAllById(productsInBooking.keySet());
        productsInWarehouse.forEach(warehouse -> {
            if (warehouse.getQuantity() < productsInBooking.get(warehouse.getProductId())) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException("Ошибка, товар из корзины не находится в требуемом количестве на складе.");
            }
        });
        for (Warehouse warehouse : productsInWarehouse) {
            warehouse.setQuantity(warehouse.getQuantity() - productsInBooking.get(warehouse.getProductId()));
        }
        booking.setOrderId(assemblyProductsForOrder.getOrderId());
        return bookingMapper.toBookedProductsDto(booking);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest) {
        Warehouse warehouse = warehouseRepository.findById(addProductToWarehouseRequest.getProductId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе.")
        );
        warehouse.setQuantity(warehouse.getQuantity() + addProductToWarehouseRequest.getQuantity());
        updateProductQuantityInShoppingStore(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getWarehouseAddress() {
        String address = Address.CURRENT_ADDRESS;
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    public BookedProductsDto bookingProducts(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> products = shoppingCartDto.getProducts();
        List<Warehouse> productsInWarehouse = warehouseRepository.findAllById(products.keySet());
        productsInWarehouse.forEach(warehouse -> {
            if (warehouse.getQuantity() < products.get(warehouse.getProductId())) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(
                        "Товар " + warehouse.getProductId() + " is sold out");
            }
        });

        double deliveryVolume = productsInWarehouse.stream()
                .map(v -> v.getDimension().getDepth() * v.getDimension().getWidth()
                        * v.getDimension().getHeight())
                .mapToDouble(Double::doubleValue)
                .sum();

        double deliveryWeight = productsInWarehouse.stream()
                .map(Warehouse::getWeight)
                .mapToDouble(Double::doubleValue)
                .sum();

        boolean fragile = productsInWarehouse.stream()
                .anyMatch(Warehouse::isFragile);

        Booking newBooking = Booking.builder()
                .shoppingCartId(shoppingCartDto.getShoppingCartId())
                .deliveryVolume(deliveryVolume)
                .deliveryWeight(deliveryWeight)
                .fragile(fragile)
                .products(products)
                .build();
        Booking booking = bookingRepository.save(newBooking);
        return bookingMapper.toBookedProductsDto(booking);
    }

    private BookedProductsDto getBookedProducts(Collection<Warehouse> productList,
                                                Map<UUID, Long> cartProducts) {
        return BookedProductsDto.builder()
                .fragile(productList.stream().anyMatch(Warehouse::isFragile))
                .deliveryWeight(productList.stream()
                        .mapToDouble(p -> p.getWeight() * cartProducts.get(p.getProductId()))
                        .sum())
                .deliveryVolume(productList.stream()
                        .mapToDouble(p ->
                                p.getDimension().getWidth() * p.getDimension().getHeight() * p.getDimension().getDepth() * cartProducts.get(p.getProductId()))
                        .sum())
                .build();
    }

    private void updateProductQuantityInShoppingStore(Warehouse product) {
        UUID productId = product.getProductId();
        QuantityState quantityState;
        Long quantity = product.getQuantity();

        if (quantity == 0) {
            quantityState = QuantityState.ENDED;
        } else if (quantity < 10) {
            quantityState = QuantityState.ENOUGH;
        } else if (quantity < 100) {
            quantityState = QuantityState.FEW;
        } else {
            quantityState = QuantityState.MANY;
        }
        shoppingStoreClient.setProductQuantityState(productId, quantityState);
    }
}
