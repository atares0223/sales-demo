package com.waaw.stock.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.stock.GoodDTO;
import com.waaw.stock.service.GoodService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodController {
    private final GoodService goodService;

    @PostMapping("/deduct")
    public ApiResponse<String> deductStock(@RequestBody @Valid List<GoodDTO> goodDTOList) {
        goodDTOList.forEach(goodService::deductStock);
        return ApiResponse.success("库存扣减成功");
    }

}
