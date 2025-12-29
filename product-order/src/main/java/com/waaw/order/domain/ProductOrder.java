package com.waaw.order.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "product_order")
@Data
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Type cannot be empty")
    @Size(min = 2, max = 10, message = "Type must be between 2 and 50 characters")
    private String type;
    @NotNull
    @DecimalMin(value = "0.0", message = "Price should not be negative")
    @DecimalMax(value = "1000.0", message = "Price should not exceed 1,000")
    private Double cost;
}
