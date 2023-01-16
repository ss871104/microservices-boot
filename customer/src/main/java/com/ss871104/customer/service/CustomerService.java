package com.ss871104.customer.service;

import com.ss871104.customer.dto.CustomerResponse;

public interface CustomerService {
    public CustomerResponse getCustomerById(Long customerId);
}
