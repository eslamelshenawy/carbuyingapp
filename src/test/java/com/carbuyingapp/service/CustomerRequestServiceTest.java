package com.carbuyingapp.service;

import com.carbuyingapp.exception.InvalidRequestStatusException;
import com.carbuyingapp.exception.ResourceNotFoundException;
import com.carbuyingapp.model.dto.CreateCustomerRequestDto;
import com.carbuyingapp.model.dto.CustomerRequestResponseDto;
import com.carbuyingapp.model.dto.UpdateRequestStatusDto;
import com.carbuyingapp.model.entity.CustomerRequest;
import com.carbuyingapp.model.enums.InspectionCompany;
import com.carbuyingapp.model.enums.RequestStatus;
import com.carbuyingapp.repository.CustomerRequestRepository;
import com.carbuyingapp.repository.SupplierOfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRequestServiceTest {

    @Mock
    private CustomerRequestRepository requestRepository;

    @Mock
    private SupplierOfferRepository offerRepository;

    @InjectMocks
    private CustomerRequestService service;

    @Test
    void createRequest_shouldSaveAndReturnResponse() {
        CreateCustomerRequestDto dto = CreateCustomerRequestDto.builder()
                .customerId(1L)
                .description("Looking for a sedan")
                .checkedByCompany(InspectionCompany.AUTO_CHECK_CO)
                .build();

        CustomerRequest saved = buildRequest(1L, 1L, RequestStatus.PENDING,
                "Looking for a sedan", InspectionCompany.AUTO_CHECK_CO);

        when(requestRepository.save(any(CustomerRequest.class))).thenReturn(saved);

        CustomerRequestResponseDto response = service.createRequest(dto);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(response.getDescription()).isEqualTo("Looking for a sedan");
        assertThat(response.getCheckedByCompany()).isEqualTo(InspectionCompany.AUTO_CHECK_CO);
        assertThat(response.getOfferCount()).isZero();
        verify(requestRepository).save(any(CustomerRequest.class));
    }

    @Test
    void listRequests_withoutStatusFilter_shouldReturnAllPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        CustomerRequest request = buildRequest(1L, 1L, RequestStatus.PENDING,
                "Test", InspectionCompany.AUTO_CHECK_CO);
        Object[] row = new Object[]{request, 3L};
        List<Object[]> rows = new java.util.ArrayList<>();
        rows.add(row);
        Page<Object[]> page = new PageImpl<>(rows, pageable, 1);

        when(requestRepository.findAllWithOfferCount(pageable)).thenReturn(page);

        Page<CustomerRequestResponseDto> result = service.listRequests(null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getOfferCount()).isEqualTo(3);
    }

    @Test
    void listRequests_withStatusFilter_shouldFilterByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        CustomerRequest request = buildRequest(1L, 1L, RequestStatus.ACTIVE,
                "Test", InspectionCompany.AUTO_CHECK_CO);
        Object[] row = new Object[]{request, 0L};
        List<Object[]> rows = new java.util.ArrayList<>();
        rows.add(row);
        Page<Object[]> page = new PageImpl<>(rows, pageable, 1);

        when(requestRepository.findByStatusWithOfferCount(eq(RequestStatus.ACTIVE), eq(pageable)))
                .thenReturn(page);

        Page<CustomerRequestResponseDto> result = service.listRequests(RequestStatus.ACTIVE, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(RequestStatus.ACTIVE);
    }

    @Test
    void getRequest_whenExists_shouldReturnResponse() {
        CustomerRequest request = buildRequest(1L, 1L, RequestStatus.ACTIVE,
                "Test", InspectionCompany.AUTO_CHECK_CO);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(offerRepository.countByRequestId(1L)).thenReturn(2L);

        CustomerRequestResponseDto response = service.getRequest(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOfferCount()).isEqualTo(2);
    }

    @Test
    void getRequest_whenNotExists_shouldThrowResourceNotFoundException() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRequest(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateStatus_fromPendingToActive_shouldSucceed() {
        CustomerRequest request = buildRequest(1L, 1L, RequestStatus.PENDING,
                "Test", InspectionCompany.AUTO_CHECK_CO);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(CustomerRequest.class))).thenAnswer(inv -> inv.getArgument(0));
        when(offerRepository.countByRequestId(1L)).thenReturn(0L);

        UpdateRequestStatusDto dto = new UpdateRequestStatusDto(RequestStatus.ACTIVE);
        CustomerRequestResponseDto response = service.updateStatus(1L, dto);

        assertThat(response.getStatus()).isEqualTo(RequestStatus.ACTIVE);
    }

    @Test
    void updateStatus_fromPendingToClosed_shouldThrowInvalidRequestStatusException() {
        CustomerRequest request = buildRequest(1L, 1L, RequestStatus.PENDING,
                "Test", InspectionCompany.AUTO_CHECK_CO);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        UpdateRequestStatusDto dto = new UpdateRequestStatusDto(RequestStatus.CLOSED);

        assertThatThrownBy(() -> service.updateStatus(1L, dto))
                .isInstanceOf(InvalidRequestStatusException.class)
                .hasMessageContaining("PENDING")
                .hasMessageContaining("CLOSED");
    }

    @Test
    void updateStatus_fromClosedToAny_shouldThrowInvalidRequestStatusException() {
        CustomerRequest request = buildRequest(1L, 1L, RequestStatus.CLOSED,
                "Test", InspectionCompany.AUTO_CHECK_CO);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        UpdateRequestStatusDto dto = new UpdateRequestStatusDto(RequestStatus.ACTIVE);

        assertThatThrownBy(() -> service.updateStatus(1L, dto))
                .isInstanceOf(InvalidRequestStatusException.class);
    }

    @Test
    void updateStatus_toSameStatus_shouldThrowInvalidRequestStatusException() {
        CustomerRequest request = buildRequest(1L, 1L, RequestStatus.ACTIVE,
                "Test", InspectionCompany.AUTO_CHECK_CO);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        UpdateRequestStatusDto dto = new UpdateRequestStatusDto(RequestStatus.ACTIVE);

        assertThatThrownBy(() -> service.updateStatus(1L, dto))
                .isInstanceOf(InvalidRequestStatusException.class)
                .hasMessageContaining("already in status");
    }

    private CustomerRequest buildRequest(Long id, Long customerId, RequestStatus status,
                                         String description, InspectionCompany company) {
        return CustomerRequest.builder()
                .id(id)
                .customerId(customerId)
                .status(status)
                .description(description)
                .checkedByCompany(company)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
