package com.waaw.order.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.common.domain.order.ProductOrderDTO;
import com.waaw.common.domain.stock.GoodDTO;
import com.waaw.order.OrderApplication;
import com.waaw.order.domain.ProductOrder;
import com.waaw.order.repository.OrderRepository;
import com.waaw.order.service.OrderService;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@ActiveProfiles("test")
@DisplayName("OrderController Tests")
@SpringBootTest(classes = { OrderApplication.class })
@AutoConfigureWebMvc
@Slf4j
class OrderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductOrder testProductOrder;
    private ProductOrderDTO testProductOrderDTO;
    private CreateOrderDTO testCreateOrderDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clear the database before each test
        orderRepository.deleteAll();

        testCreateOrderDTO = new CreateOrderDTO();
        testCreateOrderDTO.setType("STANDARD");
        testCreateOrderDTO.setCost(49.99);

        // Setup test ProductOrder without ID (will be auto-generated)
        testProductOrder = new ProductOrder();
        testProductOrder.setType("PREMIUM");
        testProductOrder.setCost(99.99);

        // Save to database to get real ID
        testProductOrder = orderService.createOrder(testCreateOrderDTO);

        // Setup test ProductOrderDTO
        testProductOrderDTO = new ProductOrderDTO();
        testProductOrderDTO.setId(testProductOrder.getId());
        testProductOrderDTO.setType("PREMIUM");
        testProductOrderDTO.setCost(99.99);

        // Setup test CreateOrderDTO

        List<GoodDTO> goodDTOList = new ArrayList<>();
        GoodDTO goodDTO = new GoodDTO();
        goodDTO.setId(1L);
        goodDTO.setQuantity(10);
        goodDTOList.add(goodDTO);
        testCreateOrderDTO.setGoodDTOList(goodDTOList);
    }

    @Test
    @DisplayName("Should get order successfully when order exists")
    void testGetOrder_Success() throws Exception {
        // Given
        Long orderId = testProductOrder.getId();

        // When & Then
        mockMvc.perform(get("/order/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(testProductOrder.getId()))
                .andExpect(jsonPath("$.data.type").value("PREMIUM"))
                .andExpect(jsonPath("$.data.cost").value(99.99));
    }

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateOrderDTO)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Should return error when stock deduction fails")
    void testCreateOrder_StockDeductionFailure() throws Exception {
        // Given - Create order with quantity > 100 to trigger insufficient stock
        CreateOrderDTO insufficientStockOrder = new CreateOrderDTO();
        insufficientStockOrder.setType("PREMIUM");
        insufficientStockOrder.setCost(199.99);
        List<GoodDTO> goodsWithHighQuantity = new ArrayList<>();
        GoodDTO goodDTO = new GoodDTO();
        goodDTO.setId(1L);
        goodDTO.setQuantity(120); // Quantity > 100 triggers insufficient stock
        goodsWithHighQuantity.add(goodDTO);
        insufficientStockOrder.setGoodDTOList(goodsWithHighQuantity);

        // When & Then
        mockMvc.perform(post("/order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(insufficientStockOrder)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.status").value(not((equalTo(200)))));
    }

    @Test
    @DisplayName("Should get auth token when token parameter is provided")
    void testGetAuthToken_WithTokenParameter() throws Exception {
        // Given
        String token = "test-token-123";

        // When & Then
        mockMvc.perform(get("/order/customer/auth/token")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data").value(token));
    }

    @Test
    @DisplayName("Should get auth token from cookie when token parameter is not provided")
    void testGetAuthToken_WithCookie() throws Exception {
        // Given
        String cookieToken = "cookie-token-456";
        Cookie cookie = new Cookie("token", cookieToken);

        // When & Then
        mockMvc.perform(get("/order/customer/auth/token")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data").value(cookieToken));
    }

    @Test
    @DisplayName("Should redirect when token is not found in parameter or cookie")
    void testGetAuthToken_NoTokenFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/order/customer/auth/token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "http://customer:8880/getCookie?key=foo&redirecturi=http://order:8881/order/customer/auth/token"));
    }

    @Test
    @DisplayName("Should prioritize token parameter over cookie")
    void testGetAuthToken_TokenParameterOverridesCookie() throws Exception {
        // Given
        String paramToken = "param-token-789";
        String cookieToken = "cookie-token-456";
        Cookie cookie = new Cookie("token", cookieToken);

        // When & Then
        mockMvc.perform(get("/order/customer/auth/token")
                .param("token", paramToken)
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data").value(paramToken));
    }

    @Test
    @DisplayName("Should return error when createOrder request is invalid")
    void testCreateOrder_InvalidRequest() throws Exception {
        // Given
        CreateOrderDTO invalidDTO = new CreateOrderDTO();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(jsonPath("$.status").value(not(equalTo(200))));
    }

    @Test
    @DisplayName("Should handle empty cookie array")
    void testGetAuthToken_EmptyCookieArray() throws Exception {
        // When & Then - No cookies provided
        mockMvc.perform(get("/order/customer/auth/token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "http://customer:8880/getCookie?key=foo&redirecturi=http://order:8881/order/customer/auth/token"));
    }

    @Test
    @DisplayName("Should handle cookie with different name")
    void testGetAuthToken_CookieWithDifferentName() throws Exception {
        // Given
        Cookie cookie = new Cookie("otherCookie", "some-value");

        // When & Then
        mockMvc.perform(get("/order/customer/auth/token")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "http://customer:8880/getCookie?key=foo&redirecturi=http://order:8881/order/customer/auth/token"));
    }

    @Test
    @DisplayName("Should handle multiple cookies and find token cookie")
    void testGetAuthToken_MultipleCookies() throws Exception {
        // Given
        Cookie tokenCookie = new Cookie("token", "multiple-cookie-token");
        Cookie otherCookie1 = new Cookie("sessionId", "session-123");
        Cookie otherCookie2 = new Cookie("userId", "user-456");

        // When & Then
        mockMvc.perform(get("/order/customer/auth/token")
                .cookie(tokenCookie, otherCookie1, otherCookie2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.data").value("multiple-cookie-token"));
    }
}
