package com.ss871104.product.repository;

import com.ss871104.product.domain.Product;
import com.ss871104.product.dto.ProductResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.id in :ids")
    List<Product> findByIds(@Param("ids") List<Long> productIdsList);

    @Query(name = "checkProductByIdAndQuantity", nativeQuery = true)
    ProductResponse checkProductByIdAndQuantity(@Param("id") Long productId, @Param("quantityNeeded") Long quantityNeeded);
}
