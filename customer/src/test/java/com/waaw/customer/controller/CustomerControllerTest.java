package com.waaw.customer.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waaw.common.domain.order.CreateOrderDTO;
import com.waaw.common.domain.stock.GoodDTO;
import com.waaw.customer.CustomerApplication;
import com.waaw.customer.domain.Customer;
import com.waaw.customer.repository.CustomerRepository;
import com.waaw.feign.api.order.OrderClient;

@ActiveProfiles("test")
@DisplayName("CustomerController Tests")
@SpringBootTest(classes = CustomerApplication.class)
@AutoConfigureWebMvc
class CustomerControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private OrderClient orderClient;

    private MockMvc mockMvc;

    private Customer testCustomer;
    private CreateOrderDTO testCreateOrderDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clear the database before each test
        customerRepository.deleteAll();

        testCustomer = new Customer();
        testCustomer.setName("John Doe");
        testCustomer.setPassword("password123");

        // Save to database to get real ID
        testCustomer = customerRepository.save(testCustomer);

        testCreateOrderDTO = new CreateOrderDTO();
        testCreateOrderDTO.setType("PREMIUM");
        testCreateOrderDTO.setCost(99.99);
        List<GoodDTO> goodDTOList = new ArrayList<>();
        GoodDTO goodDTO = new GoodDTO();
        goodDTO.setId(1L);
        goodDTO.setQuantity(2);
        goodDTOList.add(goodDTO);
        testCreateOrderDTO.setGoodDTOList(goodDTOList);
    }

    @Test
    @DisplayName("GET /{id} - Should return customer when exists")
    void testFindCustomerById_WhenExists_ShouldReturnCustomer() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/" + testCustomer.getId()))
                .andExpect(status().isOk())
                
                .andExpect(jsonPath("$.data.id").value(testCustomer.getId()))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.password").value("password123"));
    }

    @Test
    @DisplayName("GET /{id} - Should return customer when id is 2 (with delay)")
    void testFindCustomerById_WhenIdIs2_ShouldReturnWithDelay() throws Exception {
        // Given
        Customer customer2 = new Customer();
        customer2.setName("Jane Smith");
        customer2.setPassword("password456");
        customer2 = customerRepository.save(customer2);

        // Act & Assert - This should work but may take longer due to Thread.sleep(40)
        mockMvc.perform(get("/" + customer2.getId()))
                .andExpect(status().isOk())
                
                .andExpect(jsonPath("$.data.id").value(customer2.getId()))
                .andExpect(jsonPath("$.data.name").value("Jane Smith"));
    }

    @Test
    @DisplayName("GET /{id} - Should return success when customer not found")
    void testFindCustomerById_WhenNotExists_ShouldReturnSuccessWithNull() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/999"))
                .andExpect(status().isOk())
                
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET /setCookie - Should set cookie successfully")
    void testSetCookie_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/setCookie")
                        .param("key", "testKey")
                        .param("value", "testValue"))
                .andExpect(status().isOk())
                
                .andExpect(jsonPath("$.data").value("set cookie success"));
    }

    @Test
    @DisplayName("GET /getCookie - Should return cookie value when exists")
    void testGetCookie_WhenCookieExists_ShouldReturnValue() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/getCookie")
                        .param("key", "testKey")
                        .cookie(new jakarta.servlet.http.Cookie("testKey", "testValue")))
                .andExpect(status().isOk())
                
                .andExpect(jsonPath("$.data").value("testValue"));
    }

    @Test
    @DisplayName("GET /getCookie - Should return not found when cookie doesn't exist")
    void testGetCookie_WhenCookieNotExists_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/getCookie")
                        .param("key", "nonExistentKey"))
                .andExpect(status().isOk())
                
                .andExpect(jsonPath("$.data").value("Not found"));
    }

}