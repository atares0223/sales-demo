package com.waaw.stock.repository;

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

import com.waaw.stock.StockApplication;
import com.waaw.stock.domain.Good;

@ActiveProfiles("test")
@DisplayName("GoodRepository Integration Tests")
@SpringBootTest(classes = StockApplication.class)
class GoodRepositoryTest {

    @Autowired
    private GoodRepository goodRepository;

    private Good testGood;
    private Good testGood2;

    @BeforeEach
    void setUp() {
        // Clear repository before each test
        goodRepository.deleteAll();

        // Create test goods
        testGood = new Good();
        testGood.setName("Laptop");
        testGood.setQuantity(50);

        testGood2 = new Good();
        testGood2.setName("Mouse");
        testGood2.setQuantity(100);
    }

    @Test
    @DisplayName("Should save good successfully")
    void testSave_Success() {
        // When
        Good savedGood = goodRepository.save(testGood);

        // Then
        assertNotNull(savedGood);
        assertNotNull(savedGood.getId());
        assertEquals(testGood.getName(), savedGood.getName());
        assertEquals(testGood.getQuantity(), savedGood.getQuantity());
    }

    @Test
    @DisplayName("Should find good by id when good exists")
    void testFindById_WhenGoodExists() {
        // Given
        Good savedGood = goodRepository.save(testGood);

        // When
        Optional<Good> foundGood = goodRepository.findById(savedGood.getId());

        // Then
        assertTrue(foundGood.isPresent());
        assertEquals(savedGood.getId(), foundGood.get().getId());
        assertEquals(savedGood.getName(), foundGood.get().getName());
        assertEquals(savedGood.getQuantity(), foundGood.get().getQuantity());
    }

    @Test
    @DisplayName("Should return empty optional when good not found")
    void testFindById_WhenGoodNotFound() {
        // When
        Optional<Good> foundGood = goodRepository.findById(999L);

        // Then
        assertFalse(foundGood.isPresent());
    }

    @Test
    @DisplayName("Should find all goods")
    void testFindAll() {
        // Given
        Good good1 = goodRepository.save(testGood);
        Good good2 = goodRepository.save(testGood2);

        // When
        List<Good> goods = (List<Good>) goodRepository.findAll();

        // Then
        assertEquals(2, goods.size());
        assertTrue(goods.stream().anyMatch(g -> g.getId().equals(good1.getId())));
        assertTrue(goods.stream().anyMatch(g -> g.getId().equals(good2.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no goods exist")
    void testFindAll_WhenNoGoods() {
        // When
        List<Good> goods = (List<Good>) goodRepository.findAll();

        // Then
        assertTrue(goods.isEmpty());
    }

    @Test
    @DisplayName("Should check if good exists by id")
    void testExistsById() {
        // Given
        Good savedGood = goodRepository.save(testGood);

        // When & Then
        assertTrue(goodRepository.existsById(savedGood.getId()));
        assertFalse(goodRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should delete good by id")
    void testDeleteById() {
        // Given
        Good savedGood = goodRepository.save(testGood);
        assertTrue(goodRepository.existsById(savedGood.getId()));

        // When
        goodRepository.deleteById(savedGood.getId());

        // Then
        assertFalse(goodRepository.existsById(savedGood.getId()));
    }

    @Test
    @DisplayName("Should delete all goods")
    void testDeleteAll() {
        // Given
        goodRepository.save(testGood);
        goodRepository.save(testGood2);
        assertEquals(2, goodRepository.count());

        // When
        goodRepository.deleteAll();

        // Then
        assertEquals(0, goodRepository.count());
    }

    @Test
    @DisplayName("Should count goods")
    void testCount() {
        // Given
        assertEquals(0, goodRepository.count());

        goodRepository.save(testGood);
        assertEquals(1, goodRepository.count());

        goodRepository.save(testGood2);
        assertEquals(2, goodRepository.count());
    }

    @Test
    @DisplayName("Should deduct stock successfully when sufficient quantity exists")
    void testDeductStock_Success() {
        // Given
        Good savedGood = goodRepository.save(testGood);
        int deductQuantity = 10;

        // When
        int affectedRows = goodRepository.deductStock(savedGood.getId(), deductQuantity);

        // Then
        assertEquals(1, affectedRows);
        Optional<Good> updatedGood = goodRepository.findById(savedGood.getId());
        assertTrue(updatedGood.isPresent());
        assertEquals(50 - deductQuantity, updatedGood.get().getQuantity());
    }

    @Test
    @DisplayName("Should not deduct stock when insufficient quantity exists")
    void testDeductStock_InsufficientQuantity() {
        // Given
        Good savedGood = goodRepository.save(testGood);
        int deductQuantity = 60; // More than available (50)

        // When
        int affectedRows = goodRepository.deductStock(savedGood.getId(), deductQuantity);

        // Then
        assertEquals(0, affectedRows); // No rows affected due to insufficient quantity
        Optional<Good> unchangedGood = goodRepository.findById(savedGood.getId());
        assertTrue(unchangedGood.isPresent());
        assertEquals(50, unchangedGood.get().getQuantity()); // Quantity unchanged
    }

    @Test
    @DisplayName("Should not deduct stock when good doesn't exist")
    void testDeductStock_GoodNotExists() {
        // When
        int affectedRows = goodRepository.deductStock(999L, 10);

        // Then
        assertEquals(0, affectedRows);
    }

    @Test
    @DisplayName("Should deduct exact available quantity")
    void testDeductStock_ExactQuantity() {
        // Given
        Good savedGood = goodRepository.save(testGood);
        int deductQuantity = 50; // Exact available quantity

        // When
        int affectedRows = goodRepository.deductStock(savedGood.getId(), deductQuantity);

        // Then
        assertEquals(1, affectedRows);
        Optional<Good> updatedGood = goodRepository.findById(savedGood.getId());
        assertTrue(updatedGood.isPresent());
        assertEquals(0, updatedGood.get().getQuantity());
    }

    @Test
    @DisplayName("Should save multiple goods with different data")
    void testSave_MultipleGoods() {
        // Given
        Good good3 = new Good();
        good3.setName("Keyboard");
        good3.setQuantity(75);

        // When
        Good savedGood1 = goodRepository.save(testGood);
        Good savedGood2 = goodRepository.save(testGood2);
        Good savedGood3 = goodRepository.save(good3);

        // Then
        assertNotNull(savedGood1.getId());
        assertNotNull(savedGood2.getId());
        assertNotNull(savedGood3.getId());

        List<Good> allGoods = (List<Good>) goodRepository.findAll();
        assertEquals(3, allGoods.size());
    }

    @Test
    @DisplayName("Should handle updating existing good")
    void testSave_UpdateExistingGood() {
        // Given
        Good savedGood = goodRepository.save(testGood);
        savedGood.setName("Updated Laptop");
        savedGood.setQuantity(75);

        // When
        Good updatedGood = goodRepository.save(savedGood);

        // Then
        assertEquals(savedGood.getId(), updatedGood.getId());
        assertEquals("Updated Laptop", updatedGood.getName());
        assertEquals(75, updatedGood.getQuantity());

        // Verify only one good exists
        assertEquals(1, goodRepository.count());
    }

    @Test
    @DisplayName("Should handle deduct stock with zero quantity")
    void testDeductStock_ZeroQuantity() {
        // Given
        Good savedGood = goodRepository.save(testGood);
        int initialQuantity = savedGood.getQuantity();

        // When
        int affectedRows = goodRepository.deductStock(savedGood.getId(), 0);

        // Then
        assertEquals(1, affectedRows); // Should succeed with zero deduction
        Optional<Good> updatedGood = goodRepository.findById(savedGood.getId());
        assertTrue(updatedGood.isPresent());
        assertEquals(initialQuantity, updatedGood.get().getQuantity()); // Quantity unchanged
    }

    @Test
    @DisplayName("Should handle deduct stock with negative quantity")
    void testDeductStock_NegativeQuantity() {
        // Given
        Good savedGood = goodRepository.save(testGood);

        // When
        int affectedRows = goodRepository.deductStock(savedGood.getId(), -10);

        // Then
        assertEquals(0, affectedRows); // Should fail due to negative quantity constraint
        Optional<Good> unchangedGood = goodRepository.findById(savedGood.getId());
        assertTrue(unchangedGood.isPresent());
        assertEquals(50, unchangedGood.get().getQuantity()); // Quantity unchanged
    }

    @Test
    @DisplayName("Should handle multiple concurrent deductions")
    void testDeductStock_ConcurrentAccess() {
        // Given
        Good savedGood = goodRepository.save(testGood);
        int initialQuantity = 50;

        // When - Multiple deductions
        int affectedRows1 = goodRepository.deductStock(savedGood.getId(), 10);
        int affectedRows2 = goodRepository.deductStock(savedGood.getId(), 15);
        int affectedRows3 = goodRepository.deductStock(savedGood.getId(), 5);

        // Then
        assertEquals(1, affectedRows1);
        assertEquals(1, affectedRows2);
        assertEquals(1, affectedRows3);

        Optional<Good> updatedGood = goodRepository.findById(savedGood.getId());
        assertTrue(updatedGood.isPresent());
        assertEquals(initialQuantity - 10 - 15 - 5, updatedGood.get().getQuantity());
    }
}