package ru.yandex.practicum.interactionapi.exception;

public class ProductInShoppingCartNotInWarehouse extends RuntimeException {
    public ProductInShoppingCartNotInWarehouse(String message) {
        super(message);
    }
}
