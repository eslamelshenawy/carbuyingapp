package com.carbuyingapp.controller;

import com.carbuyingapp.model.dto.CreateCustomerRequestDto;
import com.carbuyingapp.model.dto.CustomerRequestResponseDto;
import com.carbuyingapp.model.dto.UpdateRequestStatusDto;
import com.carbuyingapp.model.enums.RequestStatus;
import com.carbuyingapp.service.CustomerRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Tag(name = "Customer Requests", description = "Manage car buying requests")
public class CustomerRequestController {

    private final CustomerRequestService customerRequestService;

    @PostMapping
    @Operation(summary = "Create a request", description = "Create a new car buying request specifying the car description and inspection company")
    public ResponseEntity<CustomerRequestResponseDto> createRequest(
            @Valid @RequestBody CreateCustomerRequestDto dto) {
        CustomerRequestResponseDto response = customerRequestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List requests", description = "List all customer requests with optional status filter and pagination. Returns offer count per request.")
    public ResponseEntity<Page<CustomerRequestResponseDto>> listRequests(
            @RequestParam(required = false) RequestStatus status,
            Pageable pageable) {
        Page<CustomerRequestResponseDto> page = customerRequestService.listRequests(status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a request", description = "Get a single customer request by ID with its offer count")
    public ResponseEntity<CustomerRequestResponseDto> getRequest(@PathVariable Long id) {
        CustomerRequestResponseDto response = customerRequestService.getRequest(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update request status", description = "Update the status of a customer request. Valid transitions: PENDING→ACTIVE/CANCELLED, ACTIVE→CLOSED/CANCELLED")
    public ResponseEntity<CustomerRequestResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequestStatusDto dto) {
        CustomerRequestResponseDto response = customerRequestService.updateStatus(id, dto);
        return ResponseEntity.ok(response);
    }
}
