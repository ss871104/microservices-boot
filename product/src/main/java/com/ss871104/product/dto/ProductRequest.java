package com.ss871104.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    private Long id;
    private String name;
    private Long price;
    private Long quantity;
    private Map<Long, Long> orderedProductsAndQuantity;
}
