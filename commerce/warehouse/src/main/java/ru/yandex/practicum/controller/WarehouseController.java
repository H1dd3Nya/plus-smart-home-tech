package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public void createProductToWarehouse(@RequestBody NewProductInWarehouseRequest request) {
        log.info("Добавить новый товар на склад, request --> {}", request);
        warehouseService.createProductToWarehouse(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/check")
    public BookedProductsDto checkAvailableProducts(@RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("Запрос, на проверку количества товаров shoppingCartDto --> {}", shoppingCartDto);
        return warehouseService.checkAvailableProducts(shoppingCartDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        log.info("Запрос, на добавление товара на склад, request --> {}", request);
        warehouseService.addProductToWarehouse(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/address")
    public AddressDto getWareHouseAddress() {
        log.info("Пришел запрос, на получения адреса склада для расчёта доставки.");
        return warehouseService.getWareHouseAddress();
    }
}
