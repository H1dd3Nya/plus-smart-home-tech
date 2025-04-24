package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.interactionapi.dto.AddressDto;
import ru.yandex.practicum.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.interactionapi.dto.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interactionapi.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void addNewProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest);

    void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest);

    void acceptReturn(Map<UUID, Long> products);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrder);

    void addProductToWarehouse(AddProductToWarehouseRequest requestDto);

    AddressDto getWarehouseAddress();

    BookedProductsDto bookingProducts(ShoppingCartDto shoppingCartDto);
}

