package com.ss871104.product.controller;

import com.ss871104.product.dto.ProductRequest;
import com.ss871104.product.dto.ProductResponse;
import com.ss871104.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping(params = "productIds", path = "/getProductInfo")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> retrieveProductInfo(@RequestParam List<Long> productIds) {
        log.info("get products info by productId " + productIds);

        return productService.getProductsById(productIds);
    }

    @PostMapping(path = "/updateQuantity")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<ProductResponse> orderReceivedUpdateQuantity(@RequestBody Map<Long, Long> productIdAndQuantityOrdered) {
        log.info("update products quantity");

        ProductRequest productRequest = new ProductRequest();
        productRequest.setOrderedProductsAndQuantity(productIdAndQuantityOrdered);

        return productService.updateOrderedProductsQuantity(productRequest);
    }

}
