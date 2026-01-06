package com.waaw.common.domain.order;

import lombok.Data;

@Data
public class ProductOrderDTO {
    private Long id;
    private String type;
    private Double cost;
}
