package com.waaw.stock.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.waaw.common.domain.stock.GoodDTO;
import com.waaw.stock.StockApplication;
import com.waaw.stock.domain.Good;
import com.waaw.stock.repository.GoodRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@DisplayName("GoodController Integration Tests")
@SpringBootTest(classes = StockApplication.class)
@AutoConfigureMockMvc
class GoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GoodRepository goodRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Good testGood;
    private Good testGood2;
    private GoodDTO testGoodDTO;
    private GoodDTO testGoodDTO2;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        goodRepository.deleteAll();

        testGood = new Good();
        testGood.setName("Laptop");
        testGood.setQuantity(50);

        testGood2 = new Good();
        testGood2.setName("Mouse");
        testGood2.setQuantity(100);

        // Save to database to get real IDs
        testGood = goodRepository.save(testGood);
        testGood2 = goodRepository.save(testGood2);

        // Create corresponding DTOs
        testGoodDTO = new GoodDTO();
        testGoodDTO.setId(testGood.getId());
        testGoodDTO.setQuantity(10);

        testGoodDTO2 = new GoodDTO();
        testGoodDTO2.setId(testGood2.getId());
        testGoodDTO2.setQuantity(20);
    }

    @Test
    @DisplayName("POST /goods/deduct - Should deduct stock successfully for single item")
    void testDeductStock_SingleItem_Success() throws Exception {
        // Given
        List<GoodDTO> goodDTOList = Arrays.asList(testGoodDTO);

        // When & Then
        mockMvc.perform(post("/goods/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goodDTOList)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data").value("库存扣减成功"));

        // Verify quantity was deducted
        Good updatedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(40, updatedGood.getQuantity()); // 50 - 10
    }

    @Test
    @DisplayName("POST /goods/deduct - Should deduct stock successfully for multiple items")
    void testDeductStock_MultipleItems_Success() throws Exception {
        // Given
        List<GoodDTO> goodDTOList = Arrays.asList(testGoodDTO, testGoodDTO2);

        // When & Then
        mockMvc.perform(post("/goods/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goodDTOList)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data").value("库存扣减成功"));

        // Verify quantities were deducted
        Good updatedGood1 = goodRepository.findById(testGood.getId()).orElseThrow();
        Good updatedGood2 = goodRepository.findById(testGood2.getId()).orElseThrow();
        assertEquals(40, updatedGood1.getQuantity()); // 50 - 10
        assertEquals(80, updatedGood2.getQuantity()); // 100 - 20
    }

    @Test
    @DisplayName("POST /goods/deduct - Should return error when insufficient stock for one item")
    void testDeductStock_InsufficientStock() throws Exception {
        // Given - try to deduct more than available
        testGoodDTO.setQuantity(60); // More than available (50)
        List<GoodDTO> goodDTOList = Arrays.asList(testGoodDTO);

        // When & Then
        mockMvc.perform(post("/goods/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goodDTOList)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("库存不足"));

        // Verify quantity was not changed
        Good unchangedGood = goodRepository.findById(testGood.getId()).orElseThrow();
        assertEquals(50, unchangedGood.getQuantity());
    }

    @Test
    @DisplayName("POST /goods/deduct - Should handle empty list")
    void testDeductStock_EmptyList() throws Exception {
        // Given
        List<GoodDTO> emptyList = Arrays.asList();

        // When & Then
        mockMvc.perform(post("/goods/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyList)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data").value("库存扣减成功"));
    }

    @Test
    @DisplayName("POST /goods/deduct - Should handle partial success when one item fails")
    void testDeductStock_PartialFailure() throws Exception {
        // Given - first item succeeds, second item fails
        testGoodDTO2.setQuantity(120); // More than available (100)
        List<GoodDTO> goodDTOList = Arrays.asList(testGoodDTO, testGoodDTO2);

        // When & Then
        mockMvc.perform(post("/goods/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goodDTOList)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("库存不足"));

        // Verify first item was deducted but second was not
        Good updatedGood1 = goodRepository.findById(testGood.getId()).orElseThrow();
        Good unchangedGood2 = goodRepository.findById(testGood2.getId()).orElseThrow();
        assertEquals(40, updatedGood1.getQuantity()); // 50 - 10
        assertEquals(100, unchangedGood2.getQuantity()); // Unchanged
    }

    @Test
    @DisplayName("POST /goods/deduct - Should handle non-existent goods")
    void testDeductStock_NonExistentGood() throws Exception {
        // Given
        GoodDTO nonExistentGoodDTO = new GoodDTO();
        nonExistentGoodDTO.setId(999L);
        nonExistentGoodDTO.setQuantity(10);
        List<GoodDTO> goodDTOList = Arrays.asList(nonExistentGoodDTO);

        // When & Then
        mockMvc.perform(post("/goods/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goodDTOList)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("库存不足"));
    }

}