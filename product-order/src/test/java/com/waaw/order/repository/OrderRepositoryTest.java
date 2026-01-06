package com.waaw.order.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.waaw.order.OrderApplication;
import com.waaw.order.domain.ProductOrder;

@ActiveProfiles("test")
@DisplayName("OrderRepository Tests")
@SpringBootTest(classes = OrderApplication.class)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private ProductOrder testOrder;
    private ProductOrder testOrder2;

    @BeforeEach
    void setUp() {
        // Clear repository before each test
        orderRepository.deleteAll();

        // Create test orders
        testOrder = new ProductOrder();
        testOrder.setType("PREMIUM");
        testOrder.setCost(99.99);

        testOrder2 = new ProductOrder();
        testOrder2.setType("BASIC");
        testOrder2.setCost(49.99);
    }

    @Test
    @DisplayName("Should save order successfully")
    void testSave_Success() {
        // When
        ProductOrder savedOrder = orderRepository.save(testOrder);

        // Then
        assertNotNull(savedOrder);
        assertNotNull(savedOrder.getId());
        assertEquals(testOrder.getType(), savedOrder.getType());
        assertEquals(testOrder.getCost(), savedOrder.getCost());
    }

    @Test
    @DisplayName("Should find order by id when order exists")
    void testFindById_WhenOrderExists() {
        // Given
        ProductOrder savedOrder = orderRepository.save(testOrder);

        // When
        Optional<ProductOrder> foundOrder = orderRepository.findById(savedOrder.getId());

        // Then
        assertTrue(foundOrder.isPresent());
        assertEquals(savedOrder.getId(), foundOrder.get().getId());
        assertEquals(savedOrder.getType(), foundOrder.get().getType());
        assertEquals(savedOrder.getCost(), foundOrder.get().getCost());
    }

    @Test
    @DisplayName("Should return empty optional when order not found")
    void testFindById_WhenOrderNotFound() {
        // When
        Optional<ProductOrder> foundOrder = orderRepository.findById(999L);

        // Then
        assertFalse(foundOrder.isPresent());
    }

    @Test
    @DisplayName("Should find all orders")
    void testFindAll() {
        // Given
        ProductOrder order1 = orderRepository.save(testOrder);
        ProductOrder order2 = orderRepository.save(testOrder2);

        // When
        Iterable<ProductOrder> orders = orderRepository.findAll();
        List<ProductOrder> orderList = (List<ProductOrder>) orders;

        // Then
        assertEquals(2, orderList.size());
        assertTrue(orderList.stream().anyMatch(o -> o.getId().equals(order1.getId())));
        assertTrue(orderList.stream().anyMatch(o -> o.getId().equals(order2.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void testFindAll_WhenNoOrders() {
        // When
        Iterable<ProductOrder> orders = orderRepository.findAll();
        List<ProductOrder> orderList = (List<ProductOrder>) orders;

        // Then
        assertTrue(orderList.isEmpty());
    }

    @Test
    @DisplayName("Should check if order exists by id")
    void testExistsById() {
        // Given
        ProductOrder savedOrder = orderRepository.save(testOrder);

        // When & Then
        assertTrue(orderRepository.existsById(savedOrder.getId()));
        assertFalse(orderRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should delete order by id")
    void testDeleteById() {
        // Given
        ProductOrder savedOrder = orderRepository.save(testOrder);
        assertTrue(orderRepository.existsById(savedOrder.getId()));

        // When
        orderRepository.deleteById(savedOrder.getId());

        // Then
        assertFalse(orderRepository.existsById(savedOrder.getId()));
    }

    @Test
    @DisplayName("Should delete all orders")
    void testDeleteAll() {
        // Given
        orderRepository.save(testOrder);
        orderRepository.save(testOrder2);
        assertEquals(2, getOrderCount());

        // When
        orderRepository.deleteAll();

        // Then
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("Should count orders")
    void testCount() {
        // Given
        assertEquals(0, orderRepository.count());

        orderRepository.save(testOrder);
        assertEquals(1, orderRepository.count());

        orderRepository.save(testOrder2);
        assertEquals(2, orderRepository.count());
    }

    @Test
    @DisplayName("Should save multiple orders with different data")
    void testSave_MultipleOrders() {
        // Given
        ProductOrder order3 = new ProductOrder();
        order3.setType("ENTERPRISE");
        order3.setCost(199.99);

        // When
        ProductOrder savedOrder1 = orderRepository.save(testOrder);
        ProductOrder savedOrder2 = orderRepository.save(testOrder2);
        ProductOrder savedOrder3 = orderRepository.save(order3);

        // Then
        assertNotNull(savedOrder1.getId());
        assertNotNull(savedOrder2.getId());
        assertNotNull(savedOrder3.getId());

        Iterable<ProductOrder> allOrders = orderRepository.findAll();
        List<ProductOrder> orderList = (List<ProductOrder>) allOrders;
        assertEquals(3, orderList.size());
    }

    @Test
    @DisplayName("Should handle updating existing order")
    void testSave_UpdateExistingOrder() {
        // Given
        ProductOrder savedOrder = orderRepository.save(testOrder);
        savedOrder.setType("UPDATED");
        savedOrder.setCost(149.99);

        // When
        ProductOrder updatedOrder = orderRepository.save(savedOrder);

        // Then
        assertEquals(savedOrder.getId(), updatedOrder.getId());
        assertEquals("UPDATED", updatedOrder.getType());
        assertEquals(149.99, updatedOrder.getCost());

        // Verify only one order exists
        assertEquals(1, orderRepository.count());
    }

    private long getOrderCount() {
        Iterable<ProductOrder> orders = orderRepository.findAll();
        List<ProductOrder> orderList = (List<ProductOrder>) orders;
        return orderList.size();
    }
}