package com.ss871104.order.repository;

import com.ss871104.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrdersByCustomerId(Long customerId);
}
