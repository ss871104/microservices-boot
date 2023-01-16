package com.ss871104.order.service;

import com.ss871104.order.dto.NewOrderRequest;
import com.ss871104.order.dto.OrderDetailResponse;
import com.ss871104.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    public List<OrderResponse> getOrdersByCustomerId(Long customerId);
    public List<OrderDetailResponse> getOrderDetailsByOrderId(List<Long> orderIds);
    public List<OrderResponse> getAllInfoByCustomerId(Long customerId);
    public String addNewOrder(NewOrderRequest newOrderRequest);
}
