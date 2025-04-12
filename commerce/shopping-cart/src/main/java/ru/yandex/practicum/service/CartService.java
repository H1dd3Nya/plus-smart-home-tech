package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.Map;

public interface CartService {
    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductToCart(String username, Map<String, Long> items);

    void deleteUserCart(String username);

    ShoppingCartDto changeCart(String username, Map<String, Long> items);

    ShoppingCartDto changeCountProductInCart(String username, ChangeProductQuantityRequest request);

    BookedProductsDto bookCart(String username);
}
