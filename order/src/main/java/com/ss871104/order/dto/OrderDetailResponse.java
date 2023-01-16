package com.ss871104.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
    private Long id;
    private Long orderId;
    private Long productId;
    private ProductResponse productInfo;
    private Long quantity;

}
