package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.CartRepository;
import ru.yandex.practicum.warehouse.WarehouseClient;

import java.util.Map;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        chechUser(username);
        return cartMapper.toShoppingCartDto(getCart(username));
    }

    @Transactional
    @Override
    public ShoppingCartDto addProductToCart(String username, Map<String, Long> items) {
        chechUser(username);
        ShoppingCart shoppingCart = getCart(username);
        if (shoppingCart == null) {
            shoppingCart = ShoppingCart.builder()
                    .username(username)
                    .products(items)
                    .isActive(true)
                    .build();
        } else {
            Map<String, Long> products = shoppingCart.getProducts();
            products.putAll(items);
        }
        return cartMapper.toShoppingCartDto(cartRepository.save(shoppingCart));
    }

    @Transactional
    @Override
    public void deleteUserCart(String username) {
        chechUser(username);
        ShoppingCart shoppingCart = getCart(username);
        shoppingCart.setActive(false);
        cartRepository.save(shoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto changeCart(String username, Map<String, Long> items) {
        chechUser(username);
        ShoppingCart shoppingCart = getCart(username);
        if (shoppingCart == null)
            throw new NoProductsInShoppingCartException("No cart available for user: " + username);
        shoppingCart.setProducts(items);
        return cartMapper.toShoppingCartDto(cartRepository.save(shoppingCart));
    }

    @Transactional
    @Override
    public ShoppingCartDto changeCountProductInCart(String username, ChangeProductQuantityRequest request) {
        chechUser(username);
        ShoppingCart shoppingCart = getCart(username);
        if (shoppingCart == null || !shoppingCart.getProducts().containsKey(request.getProductId()))
            throw new NoProductsInShoppingCartException("No cart available for user: " + username);
        shoppingCart.getProducts().put(request.getProductId(), request.getNewQuantity());
        return cartMapper.toShoppingCartDto(cartRepository.save(shoppingCart));
    }

    @Override
    public BookedProductsDto bookCart(String username) {
        chechUser(username);
        ShoppingCart shoppingCart = getCart(username);
        return warehouseClient.checkAvailableProducts(cartMapper.toShoppingCartDto(shoppingCart));
    }

    private void chechUser(String username) {
        if (username == null || username.isEmpty())
            throw new NotAuthorizedUserException("Не передан username");
    }

    private ShoppingCart getCart(String username) {
        return cartRepository.findByUsername(username);
    }
}
