package com.waaw.order.service;

import org.springframework.stereotype.Service;

import com.waaw.common.exception.BusinessException;
import com.waaw.order.domain.ProductOrder;
import com.waaw.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    public ProductOrder createOrder( ProductOrder productOrder){
       return orderRepository.save(productOrder);
    }

    public ProductOrder findById( Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new BusinessException("Order not found"));
    }
}
