package com.ss871104.order.controller;

import com.ss871104.order.dto.NewOrderRequest;
import com.ss871104.order.dto.OrderResponse;
import com.ss871104.order.service.OrderService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    // http://localhost:8200/api/order/info/10001 without api gateway
    // http://localhost:8083/api/order/info/10001 with api gateway
    @GetMapping("/info/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackResponse2")
    public List<OrderResponse> getAllInfoByCustomerId(@PathVariable Long customerId) {
        log.info("get all info by customerId " + customerId);

        return orderService.getAllInfoByCustomerId(customerId);
    }

    // http://localhost:8200/api/order/info/placedOrder without api gateway
    // http://localhost:8083/api/order/info/placedOrder with api gateway
    @PostMapping("/placedOrder")
    @Bulkhead(name = "default")
    public ResponseEntity<String> placedOrder(@RequestBody NewOrderRequest newOrderRequest) {
        log.info("placed order");

        return new ResponseEntity<String>(orderService.addNewOrder(newOrderRequest), HttpStatus.ACCEPTED);
    }

    @GetMapping("/test")
    @Retry(name = "default", fallbackMethod = "fallbackResponse")
    public String testCircuitBreaker() {
        log.info("test api called");

        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/dummy-api", String.class);

        return forEntity.getBody();

    }

    public String fallbackResponse(Exception e) {
        log.error(e.getMessage());

        return "sorry, something went wrong";
    }

    public List<OrderResponse> fallbackResponse2(Exception e) {
        log.error(e.getMessage());

        List<OrderResponse> list = new ArrayList<>();
        list.add(OrderResponse.builder().fallbackMessage("sorry, something went wrong").build());

        return list;
    }

}
