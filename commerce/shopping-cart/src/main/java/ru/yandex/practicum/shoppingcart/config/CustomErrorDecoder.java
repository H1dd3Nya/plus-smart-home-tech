package ru.yandex.practicum.shoppingcart.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.InternalServerErrorException;
import ru.yandex.practicum.interactionapi.exception.ProductInShoppingCartLowQuantityInWarehouseException;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 404) {
            return new ProductInShoppingCartLowQuantityInWarehouseException("Product is sold out " + response.reason());
        }
        if (response.status() == 500) {
            return new InternalServerErrorException("Server error occurred " + response.reason());
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
