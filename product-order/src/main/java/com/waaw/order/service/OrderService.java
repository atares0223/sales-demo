package com.waaw.order.service;

import com.waaw.order.domain.ProductOrder;
import com.waaw.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    public void createOrder(ProductOrder productOrder){
        orderRepository.save(productOrder);
    }
}
