package com.ss871104.order.repository;

import com.ss871104.order.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("select od from OrderDetail od where od.orderId in :orderIds")
    List<OrderDetail> findOrderDetailsByOrderId(@Param("orderIds")List<Long> orderIds);
}
