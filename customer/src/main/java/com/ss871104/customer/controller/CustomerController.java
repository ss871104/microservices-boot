package com.ss871104.customer.controller;

import com.ss871104.customer.dto.CustomerResponse;
import com.ss871104.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponse retrieveCustomerInfo(@PathVariable Long customerId) {
        log.info("get customer info by customerId " + customerId);

        return customerService.getCustomerById(customerId);
    }

}
