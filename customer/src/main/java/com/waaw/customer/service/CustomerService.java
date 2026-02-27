package com.waaw.customer.service;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.common.domain.order.ProductOrderDTO;
import com.waaw.customer.domain.Customer;
import com.waaw.customer.repository.CustomerRepository;
import com.waaw.feign.api.order.OrderClient;

import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final OrderClient orderClient;

    public Customer findById(Long id) {
        if (id == null) {
            return null;
        }
        return customerRepository.findById(id).orElse(null);
    }

    @GlobalTransactional
    public ApiResponse<ProductOrderDTO> saveTestData(CreateOrderDTO createOrderDTO) throws Throwable {
        Customer customer = new Customer();
        customer.setName(RandomStringUtils.random(10));
        customer.setPassword("123456");
        customerRepository.save(customer);
        return orderClient.createOrder(createOrderDTO);
    }

}
