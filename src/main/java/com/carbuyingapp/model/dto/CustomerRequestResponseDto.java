package com.carbuyingapp.model.dto;

import com.carbuyingapp.model.enums.InspectionCompany;
import com.carbuyingapp.model.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestResponseDto {

    private Long id;
    private Long customerId;
    private RequestStatus status;
    private String description;
    private InspectionCompany checkedByCompany;
    private long offerCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
