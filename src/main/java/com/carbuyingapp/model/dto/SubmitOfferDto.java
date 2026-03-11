package com.carbuyingapp.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitOfferDto {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotBlank(message = "Car details are required")
    @Size(max = 1000, message = "Car details must not exceed 1000 characters")
    private String carDetails;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Imported flag is required")
    private Boolean imported;
}
