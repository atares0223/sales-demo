package com.waaw.order.controller;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.order.client.StockClient;
import com.waaw.order.domain.ProductOrder;
import com.waaw.order.service.OrderService;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final ModelMapper modelMapper;
    private final StockClient stockClient;
    private final OrderService orderService;
    @PostMapping("/create")
    public ApiResponse createOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO){

        stockClient.deductGoods(createOrderDTO.getGoodDTOList());
        ProductOrder productOrder = modelMapper.map(createOrderDTO, ProductOrder.class);
        orderService.createOrder(productOrder);
        log.info("create order : {} ", productOrder);
        return ApiResponse.success(productOrder);
    }
}
