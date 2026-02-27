package com.waaw.order.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.common.exception.BusinessException;
import com.waaw.feign.api.stock.StockClient;
import com.waaw.order.domain.ProductOrder;
import com.waaw.order.repository.OrderRepository;

import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final StockClient stockClient;
    private final ModelMapper modelMapper;

    @GlobalTransactional(rollbackFor = Throwable.class)
    public ProductOrder createOrder(CreateOrderDTO createOrderDTO) {
        ProductOrder productOrder = modelMapper.map(createOrderDTO, ProductOrder.class);
        log.info("create order : {} ", productOrder);
        ProductOrder ret = orderRepository.save(productOrder);
        ApiResponse<String> apiResponse = stockClient.deductGoods(createOrderDTO.getGoodDTOList());
        log.info("deductGoods response : {} ", apiResponse);
        return ret;
    }

    public ProductOrder findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new BusinessException("Order not found"));
    }
}
