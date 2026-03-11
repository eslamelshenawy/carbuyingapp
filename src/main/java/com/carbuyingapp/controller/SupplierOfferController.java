package com.carbuyingapp.controller;

import com.carbuyingapp.model.dto.SubmitOfferDto;
import com.carbuyingapp.model.dto.SupplierOfferResponseDto;
import com.carbuyingapp.service.SupplierOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Supplier Offers", description = "Manage supplier offers on customer requests")
public class SupplierOfferController {

    private final SupplierOfferService supplierOfferService;

    @PostMapping("/requests/{requestId}/offers")
    @Operation(summary = "Submit an offer", description = "Submit a new supplier offer for an active customer request. Triggers inspection by the selected company.")
    public ResponseEntity<SupplierOfferResponseDto> submitOffer(
            @PathVariable Long requestId,
            @Valid @RequestBody SubmitOfferDto dto) {
        SupplierOfferResponseDto response = supplierOfferService.submitOffer(requestId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/requests/{requestId}/offers")
    @Operation(summary = "List offers for a request", description = "Retrieve all supplier offers for a specific customer request with pagination")
    public ResponseEntity<Page<SupplierOfferResponseDto>> getOffersForRequest(
            @PathVariable Long requestId,
            Pageable pageable) {
        Page<SupplierOfferResponseDto> offers = supplierOfferService.getOffersForRequest(requestId, pageable);
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/suppliers/{supplierId}/offers")
    @Operation(summary = "List offers by supplier", description = "Retrieve all offers submitted by a specific supplier with pagination")
    public ResponseEntity<Page<SupplierOfferResponseDto>> getOffersBySupplier(
            @PathVariable Long supplierId,
            Pageable pageable) {
        Page<SupplierOfferResponseDto> offers = supplierOfferService.getOffersBySupplier(supplierId, pageable);
        return ResponseEntity.ok(offers);
    }
}
