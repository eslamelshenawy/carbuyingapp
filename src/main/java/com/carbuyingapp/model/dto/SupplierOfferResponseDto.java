package com.carbuyingapp.model.dto;

import com.carbuyingapp.model.enums.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierOfferResponseDto {

    private Long id;
    private Long supplierId;
    private Long requestId;
    private OfferStatus status;
    private Integer inspectionScore;
    private String carDetails;
    private BigDecimal price;
    private boolean imported;
    private LocalDateTime createdAt;
}
