package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.StoreRepository;
import ru.yandex.practicum.types.ProductCategory;
import ru.yandex.practicum.types.ProductState;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> getProducts(ProductCategory category, ru.yandex.practicum.dto.Pageable pageable) {
        Pageable pageRequest = PageRequest.of(pageable.getPage(), pageable.getSize(),
                Sort.by(Sort.DEFAULT_DIRECTION, String.join(",", pageable.getSort())));
        List<Product> products = storeRepository.findAllByProductCategory(category, pageRequest);
        return productMapper.mapListProducts(products);
    }

    @Transactional
    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        if (storeRepository.getByProductId(productDto.getProductId()).isPresent())
            throw new SpecifiedProductAlreadyInWarehouseException("Такой продукт уже есть БД");
        Product product = productMapper.productDtoToProduc(productDto);
        return productMapper.productToProductDto(storeRepository.save(product));
    }

    @Transactional
    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        getProduct(productDto.getProductId());
        return productMapper.productToProductDto(
                storeRepository.save(productMapper.productDtoToProduc(productDto)));
    }

    @Transactional
    @Override
    public boolean removeProduct(String productId) {
        Product product = getProduct(productId);
        product.setProductState(ProductState.DEACTIVATE);
        storeRepository.save(product);
        return true;
    }

    @Transactional
    @Override
    public boolean changeState(SetProductQuantityStateRequest request) {
        Product product = getProduct(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        storeRepository.save(product);
        return true;
    }

    @Override
    public ProductDto getProductInfo(String productId) {
        return productMapper.productToProductDto(getProduct(productId));
    }

    private Product getProduct(String productId) {
        Optional<Product> product = storeRepository.getByProductId(productId);
        if (product.isEmpty())
            throw new ProductNotFoundException("Продукта с id = " + productId + " не существует");
        return product.get();
    }
}
