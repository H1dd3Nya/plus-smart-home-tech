package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Transactional
    @Override
    public void createProductToWarehouse(NewProductInWarehouseRequest request) {
        Optional<WarehouseProduct> product = getProduct(request.getProductId());
        if (product.isPresent())
            throw new SpecifiedProductAlreadyInWarehouseException("Такой продукт уже есть в БД");
        warehouseRepository.save(warehouseMapper.toWarehouse(request));
    }

    @Override
    public BookedProductsDto checkAvailableProducts(ShoppingCartDto shoppingCartDto) {
        Map<String, Long> products = shoppingCartDto.getProducts();
        List<WarehouseProduct> warehouseProducts = warehouseRepository.findAllById(products.keySet());
        warehouseProducts.forEach(warehouseProduct -> {
            if (warehouseProduct.getQuantity() < products.get(warehouseProduct.getProductId()))
                throw new ProductInShoppingCartLowQuantityInWarehouse("Не достаточно товара на складе");
        });

        double deliveryWeight = warehouseProducts.stream()
                .map(WarehouseProduct::getWeight)
                .mapToDouble(Double::doubleValue)
                .sum();

        double deliveryVolume = warehouseProducts.stream()
                .map(warehouseProduct -> warehouseProduct.getDimension().getDepth()
                        * warehouseProduct.getDimension().getHeight() * warehouseProduct.getDimension().getWidth())
                .mapToDouble(Double::doubleValue)
                .sum();

        boolean fragile = warehouseProducts.stream()
                .anyMatch(WarehouseProduct::isFragile);
        return BookedProductsDto.builder()
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(fragile)
                .build();
    }

    @Transactional
    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        Optional<WarehouseProduct> product = getProduct(request.getProductId());
        if (product.isEmpty())
            throw new NoSpecifiedProductInWarehouseException("Нет такого товара");
        WarehouseProduct pr = product.get();
        pr.setQuantity(pr.getQuantity() + request.getQuantity());
        warehouseRepository.save(pr);
    }

    @Override
    public AddressDto getWareHouseAddress() {
        return parseAddress(CURRENT_ADDRESS);
    }

    private AddressDto parseAddress(String address) {
        switch (address) {
            case "ADDRESS_1":
                return AddressDto.builder()
                        .country("Россия")
                        .city("Москва")
                        .street("Пушкина")
                        .house("3")
                        .flat("6")
                        .build();
            case "ADDRESS_2":
                return AddressDto.builder()
                        .country("Россия")
                        .city("Санкт-Петербург")
                        .street("Колотушкина")
                        .house("4")
                        .flat("7")
                        .build();
            default:
                throw new IllegalArgumentException("Неизвестный адрес: " + address);
        }
    }

    private Optional<WarehouseProduct> getProduct(String productId) {
        return warehouseRepository.findById(productId);
    }
}
