package com.waaw.common.domain.stock;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoodDTO {
    @NotNull
    private Long id;
    @NotNull
    @Min(value = 0, message = "Quantity must be at least 0")
    @Max(value = 120, message = "Quantity cannot exceed 120")
    private Integer quantity;
}
