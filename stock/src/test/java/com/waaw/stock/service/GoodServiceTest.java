package com.waaw.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.waaw.common.domain.stock.GoodDTO;
import com.waaw.common.exception.BusinessException;
import com.waaw.stock.StockApplication;
import com.waaw.stock.domain.Good;
import com.waaw.stock.repository.GoodRepository;

@ActiveProfiles("test")
@DisplayName("GoodService Integration Tests")
@SpringBootTest(classes = StockApplication.class, properties = {
    "spring.datasource.url=jdbc:h2:mem:goodservicetest;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "spring.cloud.nacos.config.enabled=false",
    "spring.cloud.nacos.discovery.enabled=false"
})
class GoodServiceTest {

    @Autowired
    private GoodRepository goodRepository;

    @Autowired
    private GoodService goodService;

    private Good testGood;
    private GoodDTO testGoodDTO;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        goodRepository.deleteAll();

        // Create test good
        testGood = new Good();
        testGood.setName("Laptop");
        testGood.setQuantity(50);

        // Save to database to get real ID
        testGood = goodRepository.save(testGood);

        // Create corresponding DTO
        testGoodDTO = new GoodDTO();
        testGoodDTO.setId(testGood.getId());
        testGoodDTO.setQuantity(10);
    }

    @Test
    @DisplayName("Should deduct stock successfully when sufficient quantity exists")
    void testDeductStock_Success() {
        // Given
        int initialQuantity = testGood.getQuantity();
        int deductQuantity = 10;

        testGoodDTO.setQuantity(deductQuantity);

        // When
        goodService.deductStock(testGoodDTO);

        // Then
        Good updatedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(initialQuantity - deductQuantity, updatedGood.getQuantity());
    }

    @Test
    @DisplayName("Should throw BusinessException when insufficient stock")
    void testDeductStock_InsufficientStock() {
        // Given
        int deductQuantity = 60; // More than available (50)
        testGoodDTO.setQuantity(deductQuantity);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            goodService.deductStock(testGoodDTO);
        });

        assertEquals(500, exception.getCode());
        assertEquals("库存不足", exception.getMessage());

        // Verify quantity unchanged
        Good unchangedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(50, unchangedGood.getQuantity());
    }

    @Test
    @DisplayName("Should throw BusinessException when good doesn't exist")
    void testDeductStock_GoodNotExists() {
        // Given
        GoodDTO nonExistentGoodDTO = new GoodDTO();
        nonExistentGoodDTO.setId(999L);
        nonExistentGoodDTO.setQuantity(10);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            goodService.deductStock(nonExistentGoodDTO);
        });

        assertEquals(500, exception.getCode());
        assertEquals("库存不足", exception.getMessage());
    }

    @Test
    @DisplayName("Should deduct exact available quantity")
    void testDeductStock_ExactQuantity() {
        // Given
        int deductQuantity = 50; // Exact available quantity
        testGoodDTO.setQuantity(deductQuantity);

        // When
        goodService.deductStock(testGoodDTO);

        // Then
        Good updatedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(0, updatedGood.getQuantity());
    }

    @Test
    @DisplayName("Should deduct stock multiple times successfully")
    void testDeductStock_MultipleTimes() {
        // Given
        int initialQuantity = testGood.getQuantity();

        // First deduction
        testGoodDTO.setQuantity(10);
        goodService.deductStock(testGoodDTO);

        Good updatedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(initialQuantity - 10, updatedGood.getQuantity());

        // Second deduction
        testGoodDTO.setQuantity(15);
        goodService.deductStock(testGoodDTO);

        updatedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(initialQuantity - 25, updatedGood.getQuantity());
    }

    @Test
    @DisplayName("Should handle zero quantity deduction")
    void testDeductStock_ZeroQuantity() {
        // Given
        int initialQuantity = testGood.getQuantity();
        testGoodDTO.setQuantity(0);

        // When
        goodService.deductStock(testGoodDTO);

        // Then
        Good updatedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(initialQuantity, updatedGood.getQuantity()); // Quantity unchanged
    }

    @Test
    @DisplayName("Should handle negative quantity deduction")
    void testDeductStock_NegativeQuantity() {
        // Given
        int initialQuantity = testGood.getQuantity();
        testGoodDTO.setQuantity(-5); // Negative quantity

        // When & Then - Should fail due to database constraint (quantity >= 0)
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            goodService.deductStock(testGoodDTO);
        });

        assertEquals(500, exception.getCode());
        assertEquals("库存不足", exception.getMessage());

        // Verify quantity unchanged
        Good unchangedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(initialQuantity, unchangedGood.getQuantity());
    }

    @Test
    @DisplayName("Should handle very large quantity deduction")
    void testDeductStock_VeryLargeQuantity() {
        // Given
        int initialQuantity = testGood.getQuantity();
        testGoodDTO.setQuantity(1000); // Much larger than available

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            goodService.deductStock(testGoodDTO);
        });

        assertEquals(500, exception.getCode());
        assertEquals("库存不足", exception.getMessage());

        // Verify quantity unchanged
        Good unchangedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(initialQuantity, unchangedGood.getQuantity());
    }
}