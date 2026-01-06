package com.waaw.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.waaw.customer.CustomerApplication;
import com.waaw.customer.domain.Customer;
import com.waaw.customer.repository.CustomerRepository;

@ActiveProfiles("test")
@DisplayName("CustomerService Tests")
@SpringBootTest(classes = CustomerApplication.class)
class CustomerServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        customerRepository.deleteAll();

        // Create test customer
        testCustomer = new Customer();
        testCustomer.setName("John Doe");
        testCustomer.setPassword("password123");
    }

    @Test
    @DisplayName("Should find customer by id when customer exists")
    void testFindById_WhenCustomerExists_ShouldReturnCustomer() {
        // Arrange
        Customer savedCustomer = customerRepository.save(testCustomer);

        // Act
        Customer result = customerService.findById(savedCustomer.getId());

        // Assert
        assertNotNull(result);
        assertEquals(savedCustomer.getId(), result.getId());
        assertEquals(testCustomer.getName(), result.getName());
        assertEquals(testCustomer.getPassword(), result.getPassword());
    }

    @Test
    @DisplayName("Should return null when customer not found by id")
    void testFindById_WhenCustomerNotExists_ShouldReturnNull() {
        // Act
        Customer result = customerService.findById(999L);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when id is null")
    void testFindById_WhenIdIsNull_ShouldReturnNull() {
        // Act
        Customer result = customerService.findById(null);

        // Assert
        assertNull(result);
    }
}