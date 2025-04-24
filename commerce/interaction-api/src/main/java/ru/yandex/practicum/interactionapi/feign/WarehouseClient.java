package ru.yandex.practicum.interactionapi.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interactionapi.circuitBreaker.WarehouseClientFallback;
import ru.yandex.practicum.interactionapi.dto.AddressDto;
import ru.yandex.practicum.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.interactionapi.dto.ShoppingCartDto;
import ru.yandex.practicum.interactionapi.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interactionapi.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interactionapi.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClient {

    @PutMapping
    void newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest requestDto);

    @PostMapping("/shipped")
    void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest);

    @PostMapping("/return")
    void acceptReturn(@RequestBody Map<UUID, Long> products);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest assemblyProductsForOrder);

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest requestDto);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/booking")
    BookedProductsDto bookingProducts(@RequestBody @Valid ShoppingCartDto shoppingCartDto);
}
