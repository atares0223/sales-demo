package com.waaw.common.domain.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.waaw.common.domain.stock.GoodDTO;

import lombok.Data;

@Data
public class CreateOrderDTO {
    @NotBlank(message = "Type cannot be empty")
    @Size(min = 2, max = 10, message = "Type must be between 2 and 50 characters")
    private String type;
    @NotNull
    @DecimalMin(value = "0.0", message = "Price should not be negative")
    @DecimalMax(value = "1000.0", message = "Price should not exceed 1,000")
    private Double cost;
    @NotEmpty
    @Valid
    private List<GoodDTO> goodDTOList;
}
