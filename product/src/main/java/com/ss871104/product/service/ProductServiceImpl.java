package com.ss871104.product.service;

import com.ss871104.product.domain.Product;
import com.ss871104.product.dto.ProductRequest;
import com.ss871104.product.dto.ProductResponse;
import com.ss871104.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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
        List<ProductResponse> productResponseList = new ArrayList<>();
        boolean check = true;

        for (Long key : productRequest.getOrderedProductsAndQuantity().keySet()) {
            ProductResponse productResponse = productRepository.checkProductByIdAndQuantity(key, productRequest.getOrderedProductsAndQuantity().get(key));
            if (!productResponse.isProductCheck()) {
                check = false;
            }
            productResponseList.add(productResponse);
        }

        if (check) {
            for (ProductResponse productResponse : productResponseList) {
                Product product = new Product();
                product.setId(productResponse.getId());
                product.setName(productResponse.getName());
                product.setPrice(productResponse.getPrice());
                product.setQuantity(productResponse.getTotalQuantityLeft() - productRequest.getOrderedProductsAndQuantity().get(productResponse.getId()));
                productRepository.save(product);

            }
        }
        return productResponseList;

    }

    private List<ProductResponse> mapToProductResponseList(List<Product> productList) {

        List<ProductResponse> productResponseList = new ArrayList<>();

        for (Product product : productList) {
            productResponseList.add(ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .totalQuantityLeft(product.getQuantity())
                    .build());
        }

        return productResponseList;
    }

//    private ProductResponse mapToProductResponse(Product product) {
//
//        ProductResponse.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .price(product.getPrice())
//                .totalQuantityLeft(product.getQuantity())
//                .build();
//
//
//        return productResponse;
//    }
}
