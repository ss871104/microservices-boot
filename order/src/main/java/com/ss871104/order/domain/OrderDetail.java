package com.ss871104.order.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_detail")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;
    @Column(name = "ordered_id")
    private Long orderId;
    private Long productId;
    private Long quantity;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ordered_id", nullable = false)
//    private Order order;
}
