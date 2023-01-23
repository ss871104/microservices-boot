package com.ss871104.product.service;

import com.ss871104.product.domain.Product;
import com.ss871104.product.dto.ProductRequest;
import com.ss871104.product.dto.ProductResponse;
import com.ss871104.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Override
    public List<ProductResponse> getProductsById(List<Long> productIds) {

        return mapToProductResponseList(productRepository.findByIds(productIds));
    }

    @Override
    public List<ProductResponse> updateOrderedProductsQuantity(ProductRequest productRequest) {

        List<ProductResponse> productResponseList = productRequest.getOrderedProductsAndQuantity().keySet().stream().map(x ->
            productRepository.checkProductByIdAndQuantity(x, productRequest.getOrderedProductsAndQuantity().get(x)))
                .collect(Collectors.toList());

        // check if product quantity enough for the order
        if (productResponseList.stream().allMatch(ProductResponse::isProductCheck)) {
            return productResponseList.stream().map(productResponse -> {
                    Product product = new Product();
                    product.setId(productResponse.getId());
                    product.setName(productResponse.getName());
                    product.setPrice(productResponse.getPrice());
                    product.setQuantity(productResponse.getTotalQuantityLeft() - productRequest.getOrderedProductsAndQuantity().get(productResponse.getId()));
                    productRepository.save(product);
                    return mapToProductResponse(product);
                }).collect(Collectors.toList());
        }

        return productResponseList;

    }

    private List<ProductResponse> mapToProductResponseList(List<Product> productList) {

        return productList.stream().map(product ->
            ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .totalQuantityLeft(product.getQuantity())
                    .build())
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .totalQuantityLeft(product.getQuantity())
                .productCheck(true)
                .build();

    }

}
