package com.waaw.common.domain.stock;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodDTO {
    @NotNull
    private Long id;
    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 120, message = "Quantity cannot exceed 120")
    private Integer quantity;
}
