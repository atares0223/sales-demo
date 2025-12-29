package com.waaw.common.domain.order;

import com.waaw.common.domain.stock.GoodDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.List;

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
