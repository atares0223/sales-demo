package com.waaw.stock.service;

import com.waaw.common.domain.stock.GoodDTO;
import com.waaw.common.exception.BusinessException;
import com.waaw.stock.repository.GoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class GoodService {
    private final GoodRepository goodRepository;
    @Transactional
    public void deductStock(GoodDTO goodDTO) {
        Long id = goodDTO.getId();
        Integer quantity = goodDTO.getQuantity();
        int effectRows = goodRepository.deductStock(id,quantity);
        if(effectRows == 0){
            throw new BusinessException(500,"库存不足");
        }
    }
}
