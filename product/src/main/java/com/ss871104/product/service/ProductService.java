package com.ss871104.product.service;

import com.ss871104.product.dto.ProductRequest;
import com.ss871104.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    public List<ProductResponse> getProductsById(List<Long> productIds);

    public List<ProductResponse> updateOrderedProductsQuantity(ProductRequest productRequest);
}
