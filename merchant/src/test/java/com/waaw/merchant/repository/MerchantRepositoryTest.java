package com.waaw.merchant.repository;

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

import com.waaw.merchant.Merchant;
import com.waaw.merchant.MerchantApplication;

@ActiveProfiles("test")
@DisplayName("MerchantRepository Tests")
@SpringBootTest(classes = MerchantApplication.class)
class MerchantRepositoryTest {

    @Autowired
    private MerchantRepository merchantRepository;

    private Merchant testMerchant;
    private Merchant testMerchant2;

    @BeforeEach
    void setUp() {
        // Clear repository before each test
        merchantRepository.deleteAll();

        // Create test merchants
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
        testMerchant.setWebsite("https://doe.com");
        testMerchant.setDescription("Test merchant");
        testMerchant.setStatus("ACTIVE");
        testMerchant.setCategory("Retail");

        testMerchant2 = new Merchant();
        testMerchant2.setName("Jane Smith");
        testMerchant2.setBusinessName("Smith Corp");
        testMerchant2.setEmail("jane.smith@example.com");
        testMerchant2.setPhone("0987654321");
        testMerchant2.setAddress("456 Oak St");
        testMerchant2.setCity("Los Angeles");
        testMerchant2.setState("CA");
        testMerchant2.setZip("90210");
        testMerchant2.setCountry("USA");
        testMerchant2.setTaxId("987654321");
        testMerchant2.setBusinessLicense("LIC654321");
        testMerchant2.setWebsite("https://smith.com");
        testMerchant2.setDescription("Another test merchant");
        testMerchant2.setStatus("ACTIVE");
        testMerchant2.setCategory("Technology");
    }

    @Test
    @DisplayName("Should save merchant successfully")
    void testSave_Success() {
        // When
        Merchant savedMerchant = merchantRepository.save(testMerchant);

        // Then
        assertNotNull(savedMerchant);
        assertNotNull(savedMerchant.getId());
        assertEquals(testMerchant.getName(), savedMerchant.getName());
        assertEquals(testMerchant.getEmail(), savedMerchant.getEmail());
        assertEquals(testMerchant.getBusinessName(), savedMerchant.getBusinessName());
        assertEquals(testMerchant.getPhone(), savedMerchant.getPhone());
        assertEquals(testMerchant.getAddress(), savedMerchant.getAddress());
        assertEquals(testMerchant.getCity(), savedMerchant.getCity());
        assertEquals(testMerchant.getState(), savedMerchant.getState());
        assertEquals(testMerchant.getZip(), savedMerchant.getZip());
        assertEquals(testMerchant.getCountry(), savedMerchant.getCountry());
        assertEquals(testMerchant.getTaxId(), savedMerchant.getTaxId());
        assertEquals(testMerchant.getBusinessLicense(), savedMerchant.getBusinessLicense());
        assertEquals(testMerchant.getWebsite(), savedMerchant.getWebsite());
        assertEquals(testMerchant.getDescription(), savedMerchant.getDescription());
        assertEquals(testMerchant.getStatus(), savedMerchant.getStatus());
        assertEquals(testMerchant.getCategory(), savedMerchant.getCategory());
    }

    @Test
    @DisplayName("Should find merchant by id when merchant exists")
    void testFindById_WhenMerchantExists() {
        // Given
        Merchant savedMerchant = merchantRepository.save(testMerchant);

        // When
        Optional<Merchant> foundMerchant = merchantRepository.findById(savedMerchant.getId());

        // Then
        assertTrue(foundMerchant.isPresent());
        assertEquals(savedMerchant.getId(), foundMerchant.get().getId());
        assertEquals(savedMerchant.getName(), foundMerchant.get().getName());
        assertEquals(savedMerchant.getEmail(), foundMerchant.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty optional when merchant not found")
    void testFindById_WhenMerchantNotFound() {
        // When
        Optional<Merchant> foundMerchant = merchantRepository.findById(999L);

        // Then
        assertFalse(foundMerchant.isPresent());
    }

    @Test
    @DisplayName("Should find all merchants")
    void testFindAll() {
        // Given
        Merchant merchant1 = merchantRepository.save(testMerchant);
        Merchant merchant2 = merchantRepository.save(testMerchant2);

        // When
        List<Merchant> merchants = (List<Merchant>) merchantRepository.findAll();

        // Then
        assertEquals(2, merchants.size());
        assertTrue(merchants.stream().anyMatch(m -> m.getId().equals(merchant1.getId())));
        assertTrue(merchants.stream().anyMatch(m -> m.getId().equals(merchant2.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no merchants exist")
    void testFindAll_WhenNoMerchants() {
        // When
        List<Merchant> merchants = (List<Merchant>) merchantRepository.findAll();

        // Then
        assertTrue(merchants.isEmpty());
    }

    @Test
    @DisplayName("Should check if merchant exists by id")
    void testExistsById() {
        // Given
        Merchant savedMerchant = merchantRepository.save(testMerchant);

        // When & Then
        assertTrue(merchantRepository.existsById(savedMerchant.getId()));
        assertFalse(merchantRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should delete merchant by id")
    void testDeleteById() {
        // Given
        Merchant savedMerchant = merchantRepository.save(testMerchant);
        assertTrue(merchantRepository.existsById(savedMerchant.getId()));

        // When
        merchantRepository.deleteById(savedMerchant.getId());

        // Then
        assertFalse(merchantRepository.existsById(savedMerchant.getId()));
    }

    @Test
    @DisplayName("Should delete all merchants")
    void testDeleteAll() {
        // Given
        merchantRepository.save(testMerchant);
        merchantRepository.save(testMerchant2);
        assertEquals(2, merchantRepository.count());

        // When
        merchantRepository.deleteAll();

        // Then
        assertEquals(0, merchantRepository.count());
    }

    @Test
    @DisplayName("Should count merchants")
    void testCount() {
        // Given
        assertEquals(0, merchantRepository.count());

        merchantRepository.save(testMerchant);
        assertEquals(1, merchantRepository.count());

        merchantRepository.save(testMerchant2);
        assertEquals(2, merchantRepository.count());
    }

    @Test
    @DisplayName("Should save multiple merchants with different data")
    void testSave_MultipleMerchants() {
        // Given
        Merchant merchant3 = new Merchant();
        merchant3.setName("Bob Johnson");
        merchant3.setBusinessName("Johnson LLC");
        merchant3.setEmail("bob.johnson@example.com");
        merchant3.setPhone("5555555555");
        merchant3.setAddress("789 Pine St");
        merchant3.setCity("Chicago");
        merchant3.setState("IL");
        merchant3.setZip("60601");
        merchant3.setCountry("USA");
        merchant3.setTaxId("555666777");
        merchant3.setBusinessLicense("LIC789012");
        merchant3.setWebsite("https://johnson.com");
        merchant3.setDescription("Third test merchant");
        merchant3.setStatus("INACTIVE");
        merchant3.setCategory("Services");

        // When
        Merchant savedMerchant1 = merchantRepository.save(testMerchant);
        Merchant savedMerchant2 = merchantRepository.save(testMerchant2);
        Merchant savedMerchant3 = merchantRepository.save(merchant3);

        // Then
        assertNotNull(savedMerchant1.getId());
        assertNotNull(savedMerchant2.getId());
        assertNotNull(savedMerchant3.getId());

        List<Merchant> allMerchants = (List<Merchant>) merchantRepository.findAll();
        assertEquals(3, allMerchants.size());
    }

    @Test
    @DisplayName("Should handle updating existing merchant")
    void testSave_UpdateExistingMerchant() {
        // Given
        Merchant savedMerchant = merchantRepository.save(testMerchant);
        savedMerchant.setName("Updated Name");
        savedMerchant.setBusinessName("Updated Business");
        savedMerchant.setStatus("SUSPENDED");

        // When
        Merchant updatedMerchant = merchantRepository.save(savedMerchant);

        // Then
        assertEquals(savedMerchant.getId(), updatedMerchant.getId());
        assertEquals("Updated Name", updatedMerchant.getName());
        assertEquals("Updated Business", updatedMerchant.getBusinessName());
        assertEquals("SUSPENDED", updatedMerchant.getStatus());

        // Verify only one merchant exists
        assertEquals(1, merchantRepository.count());
    }
}