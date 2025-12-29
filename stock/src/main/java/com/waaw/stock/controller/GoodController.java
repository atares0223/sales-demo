package com.waaw.stock.controller;

import com.waaw.common.ApiResponse;
import com.waaw.common.domain.stock.GoodDTO;
import com.waaw.stock.service.GoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("goods")
@RequiredArgsConstructor
public class GoodController {
    private final GoodService goodService;

    @PostMapping("/deduct")
    public ApiResponse deductStock(@RequestBody @Valid List<GoodDTO> goodDTOList){
        goodDTOList.forEach(goodDTO ->  goodService.deductStock(goodDTO));
        return ApiResponse.success("库存扣减成功");
    }

}
