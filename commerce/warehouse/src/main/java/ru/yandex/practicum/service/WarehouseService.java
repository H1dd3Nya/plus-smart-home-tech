package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.*;

public interface WarehouseService {
    void createProductToWarehouse(NewProductInWarehouseRequest request);

    BookedProductsDto checkAvailableProducts(ShoppingCartDto shoppingCartDto);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    AddressDto getWareHouseAddress();
}
