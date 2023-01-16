package com.ss871104.order;

import com.ss871104.order.dto.OrderDetailResponse;
import com.ss871104.order.dto.OrderResponse;
import com.ss871104.order.service.OrderService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Test
    public void test1() {
        List<OrderResponse> orderResponsesSample = new ArrayList<>();
        OrderResponse orderResponse1 = OrderResponse.builder().orderId(1L).totalPrice(50L).build();
        OrderResponse orderResponse2 = OrderResponse.builder().orderId(2L).totalPrice(20L).build();
        orderResponsesSample.add(orderResponse1);
        orderResponsesSample.add(orderResponse2);

        List<OrderResponse> orderResponsesTest = orderService.getOrdersByCustomerId(10001L);

        Assertions.assertThat(orderResponsesTest).isEqualTo(orderResponsesSample);

    }

    @Test
    public void test2() {
        List<OrderDetailResponse> orderDetailResponsesSample = new ArrayList<>();
        OrderDetailResponse orderDetailResponse1 = OrderDetailResponse.builder().id(101L).orderId(1L).productId(1001L).quantity(1L).build();
        OrderDetailResponse orderDetailResponse2 = OrderDetailResponse.builder().id(102L).orderId(1L).productId(1003L).quantity(2L).build();
        OrderDetailResponse orderDetailResponse3 = OrderDetailResponse.builder().id(103L).orderId(2L).productId(1002L).quantity(1L).build();
        orderDetailResponsesSample.add(orderDetailResponse1);
        orderDetailResponsesSample.add(orderDetailResponse2);
        orderDetailResponsesSample.add(orderDetailResponse3);

        List<Long> orderIds = new ArrayList<>();
        orderIds.add(1L);
        orderIds.add(2L);

        List<OrderDetailResponse> orderDetailResponsesTest = orderService.getOrderDetailsByOrderId(orderIds);

        Assertions.assertThat(orderDetailResponsesTest).isEqualTo(orderDetailResponsesSample);

    }
}
