package com.ss871104.product.domain;

import com.ss871104.product.dto.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NamedNativeQuery(
        name = "checkProductByIdAndQuantity",
        query ="select p.id as id, p.pname as name, p.price as price, p.quantity as totalQuantityLeft, case " +
                "when p.quantity >= :quantityNeeded then cast(1 as bit) " +
                "else cast(0 as bit) end as productCheck " +
                "from Product p where p.id = :id",
        resultSetMapping = "productResponseDto"
)
@SqlResultSetMapping(
        name = "productResponseDto",
        classes = @ConstructorResult(
                targetClass = ProductResponse.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "price", type = Long.class),
                        @ColumnResult(name = "totalQuantityLeft", type = Long.class),
                        @ColumnResult(name = "productCheck", type = boolean.class)
                }
        )
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pname")
    private String name;
    private Long price;
    private Long quantity;
}
