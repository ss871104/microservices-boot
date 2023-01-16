package com.ss871104.order.service;

import com.ss871104.order.domain.Order;
import com.ss871104.order.domain.OrderDetail;
import com.ss871104.order.dto.*;
import com.ss871104.order.event.OrderPlacedEvent;
import com.ss871104.order.proxy.CustomerProxy;
import com.ss871104.order.proxy.ProductProxy;
import com.ss871104.order.repository.OrderDetailRepository;
import com.ss871104.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomerProxy customerProxy;
    private final ProductProxy productProxy;
    private final KafkaTemplate kafkaTemplate;
    @Override
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {

        return mapToOrderResponseList(orderRepository.findOrdersByCustomerId(customerId));
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailsByOrderId(List<Long> orderIds) {

        return mapToOrderDetailResponseList(orderDetailRepository.findOrderDetailsByOrderId(orderIds));
    }

    public List<OrderResponse> getAllInfoByCustomerId(Long customerId) {

        // 透過customerId取得order info -----------------------------------------------------------
        List<OrderResponse> orderResponses = getOrdersByCustomerId(customerId);

        // new 一個orderIds list
        List<Long> orderIds = new ArrayList<>();
        // 將order info獲得的orderIds放進orderIds list
        orderResponses.stream().forEach(x -> orderIds.add(x.getOrderId()));

        // new 一個List<OrderDetailResponse>物件
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        // 透過orderIds取得orderDetail info --------------------------------------------------------
        orderDetailResponses = getOrderDetailsByOrderId(orderIds);

        // new 一個productIds list
        List<Long> productIds = new ArrayList<>();
        // 將orderDetail info獲得的productIds放進productIds list
        orderDetailResponses.stream().forEach((x -> productIds.add(x.getProductId())));

        // new 一個List<ProductResponse>物件
        List<ProductResponse> productResponses = new ArrayList<>();
        // 透過ProductProxy使用product api取得product info --------------------------------------------------
        productResponses = productProxy.retrieveProductInfo(productIds);
        // 參考leetcode 1，new 一個map，key為productId，value為對應的product info
        Map<Long, ProductResponse> productMap = new HashMap<>();
        productResponses.stream().forEach((x -> productMap.put(x.getId(), x)));
        // 將product info放進orderDetailResponses
        for (int i = 0; i < orderDetailResponses.size(); i++) {
            if (productMap.containsKey(orderDetailResponses.get(i).getProductId())) {
                orderDetailResponses.get(i).setProductInfo(productMap.get(orderDetailResponses.get(i).getProductId()));
            }
        }


        // 透過CustomerProxy使用customer api取得customer info -----------------------------------------------
        final CustomerResponse finalCustomerResponse = customerProxy.retrieveCustomerInfo(customerId);

        final List<OrderDetailResponse> finalOrderDetailResponses = orderDetailResponses;

        // 將orderDetail info(orderDetailResponses)放進orderResponses
        // 將customer info放進orderResponses
        orderResponses.stream().forEach((x -> {
            List<OrderDetailResponse> orderDetailResponseList = finalOrderDetailResponses.stream().filter(d -> d.getOrderId() == x.getOrderId()).collect(Collectors.toList());
            x.setOrderDetailList(orderDetailResponseList);
            x.setCustomerInfo(finalCustomerResponse);
        }));

        return orderResponses;
    }

    @Override
    public String addNewOrder(NewOrderRequest newOrderRequest) {
        // 建立訂單
        Order newOrder = new Order();
        newOrder.setCustomerId(newOrderRequest.getCustomerId());
        newOrder.setTotalPrice(newOrderRequest.getTotalPrice());
        newOrder = orderRepository.save(newOrder);

        orderRepository.flush();
        Long orderId = newOrder.getId();

        for (Long key : newOrderRequest.getProductsIdAndQuantity().keySet()) {
            OrderDetail newOrderDetail = new OrderDetail();
            newOrderDetail.setOrderId(orderId);
            newOrderDetail.setProductId(key);
            newOrderDetail.setQuantity(newOrderRequest.getProductsIdAndQuantity().get(key));
            orderDetailRepository.save(newOrderDetail);
        }

        try {
            // 確認product是否數量足夠，若足夠同時進行產品數量更新，若不足則回傳哪個productId的產品不足
            List<ProductResponse> productResponses = productProxy.orderReceivedUpdateQuantity(newOrderRequest.getProductsIdAndQuantity());
            boolean checkIfProductUpdated = true;
            for (ProductResponse productResponse : productResponses) {
                if (!productResponse.isProductCheck()) {
                    checkIfProductUpdated = false;
                    break;
                }
            }
            // 如果product有貨
            if (checkIfProductUpdated) {
                // 發通知到kafka並且送到Notification service
                LocalDateTime now = LocalDateTime.now();
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent("Order Placed Successfully, orderId: " + newOrder.getId(), now));

                return "Order Confirmed";
            } // 如果product貨不足
            else {
                // manually rollback ----------------------------------------------------------------------------------------------------------------------
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                StringBuilder result = new StringBuilder("Order Placed Failed, lack of inventory: ");
                productResponses.stream().forEach((x -> {
                    if (!x.isProductCheck())
                        result.append("\nlack of " + x.getName() + ", item left: " + x.getTotalQuantityLeft());
                }));
                // 發通知到kafka並且送到Notification service
                LocalDateTime now = LocalDateTime.now();
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(result.toString(), now));

                return result.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 發通知到kafka並且送到Notification service
            LocalDateTime now = LocalDateTime.now();
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent("Order Placed Failed, exception issue", now));

            return "something went wrong, please contact IT desk";
        }

    }

    private List<OrderResponse> mapToOrderResponseList(List<Order> orders) {

        List<OrderResponse> orderResponseList = new ArrayList<>();

        for (Order order : orders) {
            orderResponseList.add(OrderResponse.builder()
                    .orderId(order.getId())
                    .totalPrice(order.getTotalPrice())
                    .build());
        }

        return orderResponseList;
    }

    private List<OrderDetailResponse> mapToOrderDetailResponseList(List<OrderDetail> orderDetialList) {

        List<OrderDetailResponse> orderDetailResponseList = new ArrayList<>();

        for (OrderDetail orderDetail : orderDetialList) {
            orderDetailResponseList.add(OrderDetailResponse.builder()
                    .id(orderDetail.getId())
                    .orderId(orderDetail.getOrderId())
                    .productId(orderDetail.getProductId())
                    .quantity(orderDetail.getQuantity())
                    .build());
        }

        return orderDetailResponseList;
    }
}
