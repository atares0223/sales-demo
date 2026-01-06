package com.waaw.merchant.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waaw.common.ApiResponse;
import com.waaw.merchant.Merchant;
import com.waaw.merchant.service.MerchantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
@Slf4j
public class MerchantController {
    private final MerchantService merchantService;

    @GetMapping("/{id}")
    public ApiResponse<Merchant> getMerchantById( @PathVariable("id") Long id) {
        log.info("Finding merchant by id: {}", id);
        Merchant merchant = merchantService.findById(id);
        return ApiResponse.success(merchant);
    }

    @GetMapping
    public ApiResponse<List<Merchant>> getAllMerchants() {
        log.info("Finding all merchants");
        List<Merchant> merchants = merchantService.findAll();
        return ApiResponse.success(merchants);
    }

    @PostMapping
    public ApiResponse<Merchant> createMerchant( @RequestBody @Valid Merchant merchant) {
        log.info("Creating merchant: {}", merchant);
        Merchant savedMerchant = merchantService.save(merchant);
        return ApiResponse.success(savedMerchant);
    }

    @PutMapping("/{id}")
    public ApiResponse<Merchant> updateMerchant( @PathVariable("id") Long id,
                                         @RequestBody @Valid Merchant merchant) {
        log.info("Updating merchant with id: {}", id);
        Merchant updatedMerchant = merchantService.update(id, merchant);
        return ApiResponse.success(updatedMerchant);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMerchant( @PathVariable("id") Long id) {
        log.info("Deleting merchant with id: {}", id);
        merchantService.deleteById(id);
        return ApiResponse.success("Merchant deleted successfully");
    }
}