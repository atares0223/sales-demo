package com.waaw.customer.service;

import com.waaw.customer.domain.Customer;
import com.waaw.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    public Customer findById(Long id){
        if(id == null){
            return null;
        }
        return customerRepository.findById(id).orElse(null);
    }

}
