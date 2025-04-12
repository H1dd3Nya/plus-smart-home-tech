package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.StoreService;
import ru.yandex.practicum.types.ProductCategory;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class StoreController {
    private final StoreService storeService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) {
        log.info("Пришел запрос на получения списка товаров по фильтру: category --> {}, " +
                "pageable --> {}", category, pageable);
        return storeService.getProducts(category, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ProductDto createNewProduct(@RequestBody ProductDto productDto) {
        log.info("Создание нового товара productDto --> {}", productDto);
        return storeService.createNewProduct(productDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        log.info("Обновление товара productDto --> {}", productDto);
        return storeService.updateProduct(productDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestParam String productId) {
        log.info("Удалить товар из ассортимента магазина, productId --> {}", productId);
        return storeService.removeProduct(productId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/quantityState")
    public boolean changeState(SetProductQuantityStateRequest request) {
        log.info("Установка статуса по товару, request --> {}", request);
        return storeService.changeState(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{productId}")
    public ProductDto getProductInfo(@PathVariable String productId) {
        log.info("Получить сведения по товару, productId --> {}", productId);
        return storeService.getProductInfo(productId);
    }
}
