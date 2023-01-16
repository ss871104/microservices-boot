package com.ss871104.order.proxy;

import com.ss871104.order.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name="customer")
public interface CustomerProxy {
    @GetMapping("/api/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponse retrieveCustomerInfo(@PathVariable("customerId") Long customerId);
}
