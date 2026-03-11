package com.carbuyingapp.model.dto;

import com.carbuyingapp.model.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestStatusDto {

    @NotNull(message = "Status is required")
    private RequestStatus status;
}
