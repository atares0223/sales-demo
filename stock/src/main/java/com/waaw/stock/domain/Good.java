package com.waaw.stock.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "good")
@Data
public class Good {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 2, max = 10, message = "Name must be between 2 and 50 characters")
    private String name;
    @NotNull
    @Min(value = 0, message = "Quantity must be at least 0")
    @Max(value = 120, message = "Quantity cannot exceed 120")
    private Integer quantity;

}
