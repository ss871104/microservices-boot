package com.ss871104.customer.service;

import com.ss871104.customer.domain.Customer;
import com.ss871104.customer.dto.CustomerResponse;
import com.ss871104.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        Customer customer = Customer.builder()
                .id(customerId)
                .build();

        customer = customerRepository.findById(customer.getId())
                .orElse(new Customer());

        return mapToCustomerResponse(customer);

    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }
}
