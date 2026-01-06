package com.waaw.order.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.common.domain.order.ProductOrderDTO;
import com.waaw.feign.api.stock.StockClient;
import com.waaw.order.domain.ProductOrder;
import com.waaw.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/order")
@Slf4j
public class OrderController {
    private final ModelMapper modelMapper;
    private final StockClient stockClient;
    private final OrderService orderService;
    @GetMapping("/{id}")
    public ApiResponse<ProductOrderDTO> getOrder( @PathVariable("id") Long id){
        ProductOrder productOrder = orderService.findById(id);
        if(productOrder == null){
            return ApiResponse.error("Order not found");
        }
        return ApiResponse.success(modelMapper.map(productOrder, ProductOrderDTO.class));
    }

    //Throw stock is insuffer
    @PostMapping("/create")
    public ApiResponse<ProductOrderDTO> createOrder( @RequestBody @Valid CreateOrderDTO createOrderDTO) {

        ApiResponse<String> apiResponse = stockClient.deductGoods(createOrderDTO.getGoodDTOList());
        log.info("deductGoods response : {} ", apiResponse);

        ProductOrder productOrder = modelMapper.map(createOrderDTO, ProductOrder.class);
        orderService.createOrder(productOrder);
        log.info("create order : {} ", productOrder);
        return ApiResponse.success(modelMapper.map(productOrder, ProductOrderDTO.class));
    }

    @GetMapping("/customer/auth/token")
    public ApiResponse<String> getAuthToken(HttpServletRequest request, HttpServletResponse response,@RequestParam(value = "token",required = false) String token) throws IOException {
        Cookie[] cookies = request.getCookies();
        AtomicReference<String> atomicReference = new AtomicReference<>("Not found");
        if(StringUtils.hasLength(token)){
            Cookie cookie = new Cookie("token",token);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            atomicReference.set(token);
        }else{
            if(cookies!=null && cookies.length>0){
                Arrays.stream(cookies).filter(cookie -> Objects.equals(cookie.getName(),"token")).findFirst().ifPresent(cookie -> 
                    atomicReference.set(cookie.getValue())
                );
            }
        }

        if(atomicReference.get().equals("Not found")){
            response.sendRedirect("http://customer:8880/getCookie?key=foo&redirecturi=http://order:8881/order/customer/auth/token");
            return ApiResponse.success("Redirecting to customer service to get cookie");
        }
        return ApiResponse.success(atomicReference.get());
    }
}
