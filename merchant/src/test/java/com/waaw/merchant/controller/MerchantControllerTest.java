package com.waaw.merchant.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waaw.merchant.Merchant;
import com.waaw.merchant.MerchantApplication;
import com.waaw.merchant.repository.MerchantRepository;

@ActiveProfiles("test")
@DisplayName("MerchantController Tests")
@SpringBootTest(classes = MerchantApplication.class)
@AutoConfigureWebMvc
class MerchantControllerTest  {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MerchantRepository merchantRepository;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Merchant testMerchant;
    private Merchant testMerchant2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clear the database before each test
        merchantRepository.deleteAll();

        testMerchant = new Merchant();
        testMerchant.setName("John Doe");
        testMerchant.setBusinessName("Doe Enterprises");
        testMerchant.setEmail("john.doe@example.com");
        testMerchant.setPhone("1234567890");
        testMerchant.setAddress("123 Main St");
        testMerchant.setCity("New York");
        testMerchant.setState("NY");
        testMerchant.setZip("10001");
        testMerchant.setCountry("USA");
        testMerchant.setTaxId("123456789");
        testMerchant.setBusinessLicense("LIC123456");
        testMerchant.setWebsite("https://doeenterprises.com");
        testMerchant.setDescription("A sample business");
        testMerchant.setStatus("ACTIVE");
        testMerchant.setCategory("Retail");

        testMerchant2 = new Merchant();
        testMerchant2.setName("Jane Smith");
        testMerchant2.setBusinessName("Smith Corp");
        testMerchant2.setEmail("jane.smith@example.com");
        testMerchant2.setPhone("0987654321");
        testMerchant2.setAddress("456 Oak Ave");
        testMerchant2.setCity("Los Angeles");
        testMerchant2.setState("CA");
        testMerchant2.setZip("90210");
        testMerchant2.setCountry("USA");
        testMerchant2.setTaxId("987654321");
        testMerchant2.setBusinessLicense("LIC654321");
        testMerchant2.setWebsite("https://smithcorp.com");
        testMerchant2.setDescription("Another sample business");
        testMerchant2.setStatus("ACTIVE");
        testMerchant2.setCategory("Technology");
    }

    @Test
    @DisplayName("GET /merchants/{id} - Should return merchant when exists")
    void testGetMerchantById_WhenExists_ShouldReturnMerchant() throws Exception {
        // Arrange
        Merchant savedMerchant = merchantRepository.save(testMerchant);

        // Act & Assert
        mockMvc.perform(get("/merchants/" + savedMerchant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(savedMerchant.getId().intValue()))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.businessName").value("Doe Enterprises"));
    }

    @Test
    @DisplayName("GET /merchants/{id} - Should return 500 when merchant not found")
    void testGetMerchantById_WhenNotExists_ShouldReturnError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/merchants/999"))
            .andDo(print()) 
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist())
                ;
    }

    @Test
    @DisplayName("GET /merchants - Should return all merchants")
    void testGetAllMerchants_ShouldReturnAllMerchants() throws Exception {
        // Arrange
        merchantRepository.save(testMerchant);
        merchantRepository.save(testMerchant2);

        // Act & Assert
        mockMvc.perform(get("/merchants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("POST /merchants - Should create merchant successfully")
    void testCreateMerchant_ShouldCreateSuccessfully() throws Exception {
        // Arrange
        Merchant newMerchant = new Merchant();
        newMerchant.setName("New Merchant");
        newMerchant.setBusinessName("New Business");
        newMerchant.setEmail("new@example.com");
        newMerchant.setPhone("5555555555");
        newMerchant.setAddress("789 New St");
        newMerchant.setCity("New City");
        newMerchant.setState("NC");
        newMerchant.setZip("54321");
        newMerchant.setCountry("USA");
        newMerchant.setTaxId("555555555");
        newMerchant.setBusinessLicense("LIC555555");
        newMerchant.setWebsite("https://newbusiness.com");
        newMerchant.setDescription("New business description");
        newMerchant.setStatus("ACTIVE");
        newMerchant.setCategory("Services");

        // Act & Assert
        mockMvc.perform(post("/merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMerchant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("New Merchant"))
                .andExpect(jsonPath("$.data.businessName").value("New Business"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }

    @Test
    @DisplayName("PUT /merchants/{id} - Should update merchant successfully")
    void testUpdateMerchant_ShouldUpdateSuccessfully() throws Exception {
        // Arrange
        Merchant savedMerchant = merchantRepository.save(testMerchant);

        Merchant updateMerchant = new Merchant();
        updateMerchant.setName("Updated Name");
        updateMerchant.setBusinessName("Updated Business");
        updateMerchant.setEmail("updated@example.com");

        // Act & Assert
        mockMvc.perform(put("/merchants/" + savedMerchant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMerchant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(savedMerchant.getId().intValue()))
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.businessName").value("Updated Business"));
    }

    @Test
    @DisplayName("PUT /merchants/{id} - Should return 500 when updating non-existent merchant")
    void testUpdateMerchant_WhenNotExists_ShouldReturnError() throws Exception {
        // Arrange
        Merchant updateMerchant = new Merchant();
        updateMerchant.setName("Updated Name");

        // Act & Assert
        mockMvc.perform(put("/merchants/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMerchant)))
                .andExpect(jsonPath("$.status").value(500))
                ;
    }

    @Test
    @DisplayName("DELETE /merchants/{id} - Should delete merchant successfully")
    void testDeleteMerchant_ShouldDeleteSuccessfully() throws Exception {
        // Arrange
        Merchant savedMerchant = merchantRepository.save(testMerchant);

        // Act & Assert
        mockMvc.perform(delete("/merchants/" + savedMerchant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").value("Merchant deleted successfully"));

        // Verify it was actually deleted from database
        boolean exists = merchantRepository.existsById(savedMerchant.getId());
        assert !exists;
    }

    @Test
    @DisplayName("DELETE /merchants/{id} - Should return 500 when deleting non-existent merchant")
    void testDeleteMerchant_WhenNotExists_ShouldReturnError() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/merchants/999"))
        .andExpect(jsonPath("$.status").value(500));
    }
}