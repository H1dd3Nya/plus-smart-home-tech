package ru.yandex.practicum.shoppingcart.exception;

public class ProductInShoppingCartIsNotInWarehouseException extends RuntimeException {
    public ProductInShoppingCartIsNotInWarehouseException(String message) {
        super(message);
    }
}
