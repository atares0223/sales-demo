package com.waaw.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.waaw.common.exception.BusinessException;
import com.waaw.order.OrderApplication;
import com.waaw.order.domain.ProductOrder;
import com.waaw.order.repository.OrderRepository;

@ActiveProfiles("test")
@DisplayName("OrderService Tests")
@SpringBootTest(classes = OrderApplication.class)
class OrderServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    private ProductOrder testOrder;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        orderRepository.deleteAll();

        // Create test order without ID (it will be auto-generated)
        testOrder = new ProductOrder();
        testOrder.setType("PREMIUM");
        testOrder.setCost(99.99);
    }

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder_Success() {
        // Given
        ProductOrder orderToSave = new ProductOrder();
        orderToSave.setType("STANDARD");
        orderToSave.setCost(49.99);

        // When
        ProductOrder result = orderService.createOrder(orderToSave);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("STANDARD", result.getType());
        assertEquals(49.99, result.getCost());
    }

    @Test
    @DisplayName("Should create order with existing order data")
    void testCreateOrder_WithExistingOrder() {
        // When
        ProductOrder result = orderService.createOrder(testOrder);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("PREMIUM", result.getType());
        assertEquals(99.99, result.getCost());
    }

    @Test
    @DisplayName("Should find order by id when order exists")
    void testFindById_WhenOrderExists() {
        // Given
        ProductOrder savedOrder = orderRepository.save(testOrder);
        Long orderId = savedOrder.getId();

        // When
        ProductOrder result = orderService.findById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(savedOrder.getId(), result.getId());
        assertEquals(savedOrder.getType(), result.getType());
        assertEquals(savedOrder.getCost(), result.getCost());
    }

    @Test
    @DisplayName("Should throw BusinessException when order not found")
    void testFindById_WhenOrderNotFound() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.findById(nonExistentId);
        });

        assertEquals("Order not found", exception.getMessage());
    }

}
