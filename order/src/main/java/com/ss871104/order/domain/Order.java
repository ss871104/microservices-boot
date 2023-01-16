package com.ss871104.order.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ordered")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordered_id")
    private Long id;
    private Long customerId;
    private Long totalPrice;
//    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY,
//            cascade = CascadeType.ALL)
//    private List<OrderDetail> orderDetailList;
}
