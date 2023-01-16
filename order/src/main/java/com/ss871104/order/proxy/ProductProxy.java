package com.ss871104.order.proxy;

import com.ss871104.order.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name="product")
public interface ProductProxy {
    @GetMapping(params = "productIds", value = "/api/product/getProductInfo")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> retrieveProductInfo(@RequestParam("productIds") List<Long> productIds);

    @PostMapping("/api/product/updateQuantity")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<ProductResponse> orderReceivedUpdateQuantity(@RequestBody Map<Long, Long> productIdAndQuantityOrdered);

}
