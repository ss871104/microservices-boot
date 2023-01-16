package com.ss871104.customer;

import com.ss871104.customer.dto.CustomerResponse;
import com.ss871104.customer.service.CustomerService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CustomerServiceTest {
    @Autowired
    private CustomerService customerService;

    @Test
    public void test() {
        CustomerResponse customerResponseSample = new CustomerResponse();
        customerResponseSample = customerResponseSample.builder().id(10001L).name("andy").email("andy@gmail.com").build();
        CustomerResponse customerResponseTest = new CustomerResponse();
        customerResponseTest = customerService.getCustomerById(10001L);
        Assertions.assertThat(customerResponseTest).isEqualTo(customerResponseSample);
    }
}
