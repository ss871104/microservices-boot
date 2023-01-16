package com.ss871104.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private CustomerResponse customerInfo;
    private Long totalPrice;
    private List<OrderDetailResponse> orderDetailList;
    private String fallbackMessage;

    public OrderResponse(String fallbackMessage) {
        this.fallbackMessage = fallbackMessage;
    }
}
