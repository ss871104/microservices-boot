package com.ss871104.product;

import com.ss871104.product.controller.ProductController;
import com.ss871104.product.dto.ProductResponse;
import com.ss871104.product.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductController productController;

    @Test
    public void test() {
        List<ProductResponse> productResponsesTest = new ArrayList<>();
        List<Long> productTest = new ArrayList<>();
        productTest.add(1001L);
        productTest.add(1002L);

        productResponsesTest = productService.getProductsById(productTest);

        List<ProductResponse> productResponsesSample = new ArrayList<>();
        ProductResponse productResponse1 = ProductResponse.builder().id(1001L).name("pen").price(20L).totalQuantityLeft(100L).build();
        ProductResponse productResponse2 = ProductResponse.builder().id(1002L).name("pencil").price(10L).totalQuantityLeft(200L).build();
        productResponsesSample.add(productResponse1);
        productResponsesSample.add(productResponse2);

        Assertions.assertThat(productResponsesTest).isEqualTo(productResponsesSample);
    }

    @Test
    public void test2() {
        Map<Long, Long> map = new HashMap<>();
        map.put(1001L, 101L);
        map.put(1002L, 10L);
        List<Long> productTest = new ArrayList<>();
        productTest.add(1001L);
        productTest.add(1001L);

        System.out.println(productController.orderReceivedUpdateQuantity(map));
        System.out.println(productService.getProductsById(productTest));
    }

}
