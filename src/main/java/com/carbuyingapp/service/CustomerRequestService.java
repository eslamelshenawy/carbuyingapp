package com.carbuyingapp.service;

import com.carbuyingapp.exception.InvalidRequestStatusException;
import com.carbuyingapp.exception.ResourceNotFoundException;
import com.carbuyingapp.model.dto.CreateCustomerRequestDto;
import com.carbuyingapp.model.dto.CustomerRequestResponseDto;
import com.carbuyingapp.model.dto.UpdateRequestStatusDto;
import com.carbuyingapp.model.entity.CustomerRequest;
import com.carbuyingapp.model.enums.RequestStatus;
import com.carbuyingapp.repository.CustomerRequestRepository;
import com.carbuyingapp.repository.SupplierOfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerRequestService {

    private final CustomerRequestRepository requestRepository;
    private final SupplierOfferRepository offerRepository;

    @Transactional
    public CustomerRequestResponseDto createRequest(CreateCustomerRequestDto dto) {
        CustomerRequest request = CustomerRequest.builder()
                .customerId(dto.getCustomerId())
                .description(dto.getDescription())
                .checkedByCompany(dto.getCheckedByCompany())
                .status(RequestStatus.PENDING)
                .build();

        CustomerRequest saved = requestRepository.save(request);
        log.info("Created customer request id={} for customerId={} with inspection company={}",
                saved.getId(), saved.getCustomerId(), saved.getCheckedByCompany());
        return toResponseDto(saved, 0);
    }

    @Transactional(readOnly = true)
    public Page<CustomerRequestResponseDto> listRequests(RequestStatus status, Pageable pageable) {
        Page<Object[]> page;
        if (status != null) {
            page = requestRepository.findByStatusWithOfferCount(status, pageable);
        } else {
            page = requestRepository.findAllWithOfferCount(pageable);
        }
        return page.map(row -> {
            CustomerRequest request = (CustomerRequest) row[0];
            long offerCount = (Long) row[1];
            return toResponseDto(request, offerCount);
        });
    }

    @Transactional(readOnly = true)
    public CustomerRequestResponseDto getRequest(Long id) {
        CustomerRequest request = findRequestOrThrow(id);
        long offerCount = offerRepository.countByRequestId(id);
        return toResponseDto(request, offerCount);
    }

    @Transactional
    public CustomerRequestResponseDto updateStatus(Long id, UpdateRequestStatusDto dto) {
        CustomerRequest request = findRequestOrThrow(id);
        validateStatusTransition(request.getStatus(), dto.getStatus());
        request.setStatus(dto.getStatus());
        CustomerRequest updated = requestRepository.save(request);
        long offerCount = offerRepository.countByRequestId(id);
        log.info("Updated request id={} status from {} to {}", id, request.getStatus(), dto.getStatus());
        return toResponseDto(updated, offerCount);
    }

    public CustomerRequest findRequestOrThrow(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer request not found with id: " + id));
    }

    private void validateStatusTransition(RequestStatus current, RequestStatus target) {
        if (current == target) {
            throw new InvalidRequestStatusException(
                    "Request is already in status: " + current);
        }

        boolean valid = switch (current) {
            case PENDING -> target == RequestStatus.ACTIVE || target == RequestStatus.CANCELLED;
            case ACTIVE -> target == RequestStatus.CLOSED || target == RequestStatus.CANCELLED;
            case CLOSED, CANCELLED -> false;
        };

        if (!valid) {
            throw new InvalidRequestStatusException(
                    String.format("Cannot transition from %s to %s", current, target));
        }
    }

    private CustomerRequestResponseDto toResponseDto(CustomerRequest entity, long offerCount) {
        return CustomerRequestResponseDto.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .checkedByCompany(entity.getCheckedByCompany())
                .offerCount(offerCount)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
