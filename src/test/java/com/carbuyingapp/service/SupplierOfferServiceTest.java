package com.carbuyingapp.service;

import com.carbuyingapp.exception.DuplicateOfferException;
import com.carbuyingapp.exception.InvalidRequestStatusException;
import com.carbuyingapp.inspection.InspectionResult;
import com.carbuyingapp.inspection.InspectionService;
import com.carbuyingapp.inspection.InspectionServiceFactory;
import com.carbuyingapp.model.dto.SubmitOfferDto;
import com.carbuyingapp.model.dto.SupplierOfferResponseDto;
import com.carbuyingapp.model.entity.CustomerRequest;
import com.carbuyingapp.model.entity.SupplierOffer;
import com.carbuyingapp.model.enums.InspectionCompany;
import com.carbuyingapp.model.enums.OfferStatus;
import com.carbuyingapp.model.enums.RequestStatus;
import com.carbuyingapp.repository.SupplierOfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierOfferServiceTest {

    @Mock
    private SupplierOfferRepository offerRepository;

    @Mock
    private CustomerRequestService customerRequestService;

    @Mock
    private InspectionServiceFactory inspectionServiceFactory;

    @Mock
    private InspectionService inspectionService;

    @InjectMocks
    private SupplierOfferService service;

    @Test
    void submitOffer_happyPath_shouldSaveOfferAndRunInspection() {
        Long requestId = 1L;
        CustomerRequest request = buildActiveRequest(requestId);
        SubmitOfferDto dto = SubmitOfferDto.builder()
                .supplierId(10L)
                .carDetails("2023 Toyota Camry")
                .price(new BigDecimal("25000.00"))
                .imported(false)
                .build();

        when(customerRequestService.findRequestOrThrow(requestId)).thenReturn(request);
        when(offerRepository.existsBySupplierIdAndRequestId(10L, requestId)).thenReturn(false);
        when(offerRepository.save(any(SupplierOffer.class))).thenAnswer(inv -> {
            SupplierOffer offer = inv.getArgument(0);
            offer.setId(1L);
            offer.setCreatedAt(LocalDateTime.now());
            return offer;
        });
        when(inspectionServiceFactory.getService(InspectionCompany.AUTO_CHECK_CO))
                .thenReturn(inspectionService);
        when(inspectionService.inspect(any(SupplierOffer.class)))
                .thenReturn(new InspectionResult(75, "Passed"));

        SupplierOfferResponseDto response = service.submitOffer(requestId, dto);

        assertThat(response.getSupplierId()).isEqualTo(10L);
        assertThat(response.getRequestId()).isEqualTo(requestId);
        assertThat(response.getCarDetails()).isEqualTo("2023 Toyota Camry");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("25000.00"));
        assertThat(response.getInspectionScore()).isEqualTo(75);
        assertThat(response.isImported()).isFalse();
    }

    @Test
    void submitOffer_onNonActiveRequest_shouldThrowInvalidRequestStatusException() {
        Long requestId = 1L;
        CustomerRequest request = CustomerRequest.builder()
                .id(requestId)
                .customerId(1L)
                .status(RequestStatus.PENDING)
                .description("Test")
                .checkedByCompany(InspectionCompany.AUTO_CHECK_CO)
                .build();

        SubmitOfferDto dto = SubmitOfferDto.builder()
                .supplierId(10L)
                .carDetails("Test car")
                .price(new BigDecimal("20000"))
                .imported(false)
                .build();

        when(customerRequestService.findRequestOrThrow(requestId)).thenReturn(request);

        assertThatThrownBy(() -> service.submitOffer(requestId, dto))
                .isInstanceOf(InvalidRequestStatusException.class)
                .hasMessageContaining("ACTIVE");

        verify(offerRepository, never()).save(any());
    }

    @Test
    void submitOffer_duplicateOffer_shouldThrowDuplicateOfferException() {
        Long requestId = 1L;
        CustomerRequest request = buildActiveRequest(requestId);

        SubmitOfferDto dto = SubmitOfferDto.builder()
                .supplierId(10L)
                .carDetails("Test car")
                .price(new BigDecimal("20000"))
                .imported(false)
                .build();

        when(customerRequestService.findRequestOrThrow(requestId)).thenReturn(request);
        when(offerRepository.existsBySupplierIdAndRequestId(10L, requestId)).thenReturn(true);

        assertThatThrownBy(() -> service.submitOffer(requestId, dto))
                .isInstanceOf(DuplicateOfferException.class)
                .hasMessageContaining("10")
                .hasMessageContaining("1");

        verify(offerRepository, never()).save(any());
    }

    private CustomerRequest buildActiveRequest(Long id) {
        return CustomerRequest.builder()
                .id(id)
                .customerId(1L)
                .status(RequestStatus.ACTIVE)
                .description("Test request")
                .checkedByCompany(InspectionCompany.AUTO_CHECK_CO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
