package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;

import java.util.Map;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class CartController {
    private final CartService cartService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("Пришел запрос на получения корзны пользователся: username --> {}", username);
        return cartService.getShoppingCart(username);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ShoppingCartDto addProductToCart(@RequestParam String username,
                                            @RequestBody Map<String, Long> items) {
        log.info("Добавить товары в корзину username --> {}, items --> {}", username, items);
        return cartService.addProductToCart(username, items);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void deleteUserCart(@RequestParam String username) {
        log.info("Деактивация корзины товаров для пользователя username --> {}", username);
        cartService.deleteUserCart(username);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/remove")
    public ShoppingCartDto changeCart(@RequestParam String username,
                                      @RequestBody Map<String, Long> items) {
        log.info("Изменить состав товаров в корзине username --> {}, items --> {}", username, items);
        return cartService.changeCart(username, items);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeCountProductInCart(@RequestParam String username,
                                                    @RequestBody ChangeProductQuantityRequest request) {
        log.info("Изменить количество товаров в корзине username --> {}, request --> {}", username, request);
        return cartService.changeCountProductInCart(username, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/booking")
    public BookedProductsDto bookCart(@RequestParam String username) {
        log.info("Зарезервировать товары на складе username --> {}", username);
        return cartService.bookCart(username);
    }
}
