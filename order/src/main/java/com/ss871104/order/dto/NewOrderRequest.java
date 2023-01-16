package com.ss871104.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewOrderRequest {
    private Long customerId;
    private Long totalPrice;
    private Map<Long, Long> productsIdAndQuantity;
}
