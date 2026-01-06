package com.waaw.customer.repository;

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

import com.waaw.customer.CustomerApplication;
import com.waaw.customer.domain.Customer;

@ActiveProfiles("test")
@DisplayName("CustomerRepository Tests")
@SpringBootTest(classes = CustomerApplication.class)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;
    private Customer testCustomer2;

    @BeforeEach
    void setUp() {
        // Clear repository before each test
        customerRepository.deleteAll();

        // Create test customers
        testCustomer = new Customer();
        testCustomer.setName("John Doe");
        testCustomer.setPassword("password123");

        testCustomer2 = new Customer();
        testCustomer2.setName("Jane Smith");
        testCustomer2.setPassword("password456");
    }

    @Test
    @DisplayName("Should save customer successfully")
    void testSave_Success() {
        // When
        Customer savedCustomer = customerRepository.save(testCustomer);

        // Then
        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());
        assertEquals(testCustomer.getName(), savedCustomer.getName());
        assertEquals(testCustomer.getPassword(), savedCustomer.getPassword());
    }

    @Test
    @DisplayName("Should find customer by id when customer exists")
    void testFindById_WhenCustomerExists() {
        // Given
        Customer savedCustomer = customerRepository.save(testCustomer);

        // When
        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());

        // Then
        assertTrue(foundCustomer.isPresent());
        assertEquals(savedCustomer.getId(), foundCustomer.get().getId());
        assertEquals(savedCustomer.getName(), foundCustomer.get().getName());
        assertEquals(savedCustomer.getPassword(), foundCustomer.get().getPassword());
    }

    @Test
    @DisplayName("Should return empty optional when customer not found")
    void testFindById_WhenCustomerNotFound() {
        // When
        Optional<Customer> foundCustomer = customerRepository.findById(999L);

        // Then
        assertFalse(foundCustomer.isPresent());
    }

    @Test
    @DisplayName("Should find all customers")
    void testFindAll() {
        // Given
        Customer customer1 = customerRepository.save(testCustomer);
        Customer customer2 = customerRepository.save(testCustomer2);

        // When
        List<Customer> customers = (List<Customer>) customerRepository.findAll();

        // Then
        assertEquals(2, customers.size());
        assertTrue(customers.stream().anyMatch(c -> c.getId().equals(customer1.getId())));
        assertTrue(customers.stream().anyMatch(c -> c.getId().equals(customer2.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void testFindAll_WhenNoCustomers() {
        // When
        List<Customer> customers = (List<Customer>) customerRepository.findAll();

        // Then
        assertTrue(customers.isEmpty());
    }

    @Test
    @DisplayName("Should check if customer exists by id")
    void testExistsById() {
        // Given
        Customer savedCustomer = customerRepository.save(testCustomer);

        // When & Then
        assertTrue(customerRepository.existsById(savedCustomer.getId()));
        assertFalse(customerRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should delete customer by id")
    void testDeleteById() {
        // Given
        Customer savedCustomer = customerRepository.save(testCustomer);
        assertTrue(customerRepository.existsById(savedCustomer.getId()));

        // When
        customerRepository.deleteById(savedCustomer.getId());

        // Then
        assertFalse(customerRepository.existsById(savedCustomer.getId()));
    }

    @Test
    @DisplayName("Should delete all customers")
    void testDeleteAll() {
        // Given
        customerRepository.save(testCustomer);
        customerRepository.save(testCustomer2);
        assertEquals(2, customerRepository.count());

        // When
        customerRepository.deleteAll();

        // Then
        assertEquals(0, customerRepository.count());
    }

    @Test
    @DisplayName("Should count customers")
    void testCount() {
        // Given
        assertEquals(0, customerRepository.count());

        customerRepository.save(testCustomer);
        assertEquals(1, customerRepository.count());

        customerRepository.save(testCustomer2);
        assertEquals(2, customerRepository.count());
    }

    @Test
    @DisplayName("Should save multiple customers with different data")
    void testSave_MultipleCustomers() {
        // Given
        Customer customer3 = new Customer();
        customer3.setName("Bob Johnson");
        customer3.setPassword("password789");

        // When
        Customer savedCustomer1 = customerRepository.save(testCustomer);
        Customer savedCustomer2 = customerRepository.save(testCustomer2);
        Customer savedCustomer3 = customerRepository.save(customer3);

        // Then
        assertNotNull(savedCustomer1.getId());
        assertNotNull(savedCustomer2.getId());
        assertNotNull(savedCustomer3.getId());

        List<Customer> allCustomers = (List<Customer>) customerRepository.findAll();
        assertEquals(3, allCustomers.size());
    }

    @Test
    @DisplayName("Should handle updating existing customer")
    void testSave_UpdateExistingCustomer() {
        // Given
        Customer savedCustomer = customerRepository.save(testCustomer);
        savedCustomer.setName("Updated Name");
        savedCustomer.setPassword("newpassword");

        // When
        Customer updatedCustomer = customerRepository.save(savedCustomer);

        // Then
        assertEquals(savedCustomer.getId(), updatedCustomer.getId());
        assertEquals("Updated Name", updatedCustomer.getName());
        assertEquals("newpassword", updatedCustomer.getPassword());

        // Verify only one customer exists
        assertEquals(1, customerRepository.count());
    }
}