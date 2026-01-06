package com.waaw.merchant.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.waaw.common.exception.BusinessException;
import com.waaw.merchant.Merchant;
import com.waaw.merchant.MerchantApplication;
import com.waaw.merchant.repository.MerchantRepository;

@ActiveProfiles("test")
@DisplayName("MerchantService Tests")
@SpringBootTest(classes = MerchantApplication.class)
class MerchantServiceTest {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MerchantService merchantService;

    private Merchant testMerchant;
    private Merchant testMerchant2;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Should find merchant by id when exists")
    void testFindById_WhenMerchantExists_ShouldReturnMerchant() {
        // Arrange
        Merchant savedMerchant = merchantRepository.save(testMerchant);

        // Act
        Merchant result = merchantService.findById(savedMerchant.getId());

        // Assert
        assertNotNull(result);
        assertEquals(savedMerchant.getId(), result.getId());
        assertEquals(testMerchant.getName(), result.getName());
        assertEquals(testMerchant.getBusinessName(), result.getBusinessName());
        assertEquals(testMerchant.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when merchant not found by id")
    void testFindById_WhenMerchantNotExists_ShouldThrowException() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            merchantService.findById(999L);
        });
        assertEquals("Merchant not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should return all merchants")
    void testFindAll_ShouldReturnAllMerchants() {
        // Arrange
        merchantRepository.save(testMerchant);
        merchantRepository.save(testMerchant2);

        // Act
        List<Merchant> result = merchantService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify both merchants are present
        boolean merchant1Found = result.stream()
                .anyMatch(m -> "John Doe".equals(m.getName()));
        boolean merchant2Found = result.stream()
                .anyMatch(m -> "Jane Smith".equals(m.getName()));

        assertTrue(merchant1Found);
        assertTrue(merchant2Found);
    }

    @Test
    @DisplayName("Should return empty list when no merchants exist")
    void testFindAll_WhenNoMerchants_ShouldReturnEmptyList() {
        // Act
        List<Merchant> result = merchantService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should save merchant successfully")
    void testSave_ShouldSaveMerchant() {
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

        // Act
        Merchant result = merchantService.save(newMerchant);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("New Merchant", result.getName());
        assertEquals("New Business", result.getBusinessName());
        assertEquals("new@example.com", result.getEmail());

        // Verify it was actually saved to database
        Merchant fromDb = merchantRepository.findById(result.getId()).orElse(null);
        assertNotNull(fromDb);
        assertEquals("New Merchant", fromDb.getName());
    }

    @Test
    @DisplayName("Should update merchant successfully")
    void testUpdate_WhenMerchantExists_ShouldUpdateSuccessfully() {
        // Arrange
        Merchant savedMerchant = merchantRepository.save(testMerchant);

        Merchant updateMerchant = new Merchant();
        updateMerchant.setName("Updated Name");
        updateMerchant.setBusinessName("Updated Business");
        updateMerchant.setEmail("updated@example.com");
        updateMerchant.setPhone("1111111111");
        updateMerchant.setAddress("Updated Address");
        updateMerchant.setCity("Updated City");
        updateMerchant.setState("US");
        updateMerchant.setZip("12345");
        updateMerchant.setCountry("USA");
        updateMerchant.setTaxId("111111111");
        updateMerchant.setBusinessLicense("LIC111111");
        updateMerchant.setWebsite("https://updated.com");
        updateMerchant.setDescription("Updated description");
        updateMerchant.setStatus("INACTIVE");
        updateMerchant.setCategory("Updated Category");

        // Act
        Merchant result = merchantService.update(savedMerchant.getId(), updateMerchant);

        // Assert
        assertNotNull(result);
        assertEquals(savedMerchant.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Business", result.getBusinessName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("1111111111", result.getPhone());
        assertEquals("INACTIVE", result.getStatus());

        // Verify it was actually updated in database
        Merchant fromDb = merchantRepository.findById(savedMerchant.getId()).orElse(null);
        assertNotNull(fromDb);
        assertEquals("Updated Name", fromDb.getName());
        assertEquals("INACTIVE", fromDb.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent merchant")
    void testUpdate_WhenMerchantNotExists_ShouldThrowException() {
        // Arrange
        Merchant updateMerchant = new Merchant();
        updateMerchant.setName("Updated Name");

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            merchantService.update(999L, updateMerchant);
        });
        assertEquals("Merchant not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete merchant successfully")
    void testDeleteById_WhenMerchantExists_ShouldDeleteSuccessfully() {
        // Arrange
        Merchant savedMerchant = merchantRepository.save(testMerchant);

        // Act
        merchantService.deleteById(savedMerchant.getId());

        // Assert - verify it was actually deleted from database
        boolean exists = merchantRepository.existsById(savedMerchant.getId());
        assertTrue(!exists);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent merchant")
    void testDeleteById_WhenMerchantNotExists_ShouldThrowException() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            merchantService.deleteById(999L);
        });
        assertEquals("Merchant not found with id: 999", exception.getMessage());
    }
}