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
import com.carbuyingapp.model.enums.OfferStatus;
import com.carbuyingapp.model.enums.RequestStatus;
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
public class SupplierOfferService {

    private final SupplierOfferRepository offerRepository;
    private final CustomerRequestService customerRequestService;
    private final InspectionServiceFactory inspectionServiceFactory;

    @Transactional
    public SupplierOfferResponseDto submitOffer(Long requestId, SubmitOfferDto dto) {
        CustomerRequest request = customerRequestService.findRequestOrThrow(requestId);

        if (request.getStatus() != RequestStatus.ACTIVE) {
            throw new InvalidRequestStatusException(
                    "Offers can only be submitted for ACTIVE requests. Current status: "
                            + request.getStatus());
        }

        if (offerRepository.existsBySupplierIdAndRequestId(dto.getSupplierId(), requestId)) {
            throw new DuplicateOfferException(
                    String.format("Supplier %d has already submitted an offer for request %d",
                            dto.getSupplierId(), requestId));
        }

        SupplierOffer offer = SupplierOffer.builder()
                .supplierId(dto.getSupplierId())
                .request(request)
                .status(OfferStatus.PENDING)
                .carDetails(dto.getCarDetails())
                .price(dto.getPrice())
                .imported(dto.getImported())
                .build();

        SupplierOffer saved = offerRepository.save(offer);
        log.info("Supplier {} submitted offer id={} for request id={}", dto.getSupplierId(), saved.getId(), requestId);

        InspectionService inspectionService =
                inspectionServiceFactory.getService(request.getCheckedByCompany());
        InspectionResult result = inspectionService.inspect(saved);
        saved.setInspectionScore(result.getScore());
        saved = offerRepository.save(saved);

        log.info("Inspection completed for offer id={} by {} — score: {}",
                saved.getId(), request.getCheckedByCompany(), result.getScore());

        return toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<SupplierOfferResponseDto> getOffersForRequest(Long requestId, Pageable pageable) {
        customerRequestService.findRequestOrThrow(requestId);
        return offerRepository.findByRequestId(requestId, pageable)
                .map(this::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<SupplierOfferResponseDto> getOffersBySupplier(Long supplierId, Pageable pageable) {
        return offerRepository.findBySupplierId(supplierId, pageable)
                .map(this::toResponseDto);
    }

    private SupplierOfferResponseDto toResponseDto(SupplierOffer entity) {
        return SupplierOfferResponseDto.builder()
                .id(entity.getId())
                .supplierId(entity.getSupplierId())
                .requestId(entity.getRequest().getId())
                .status(entity.getStatus())
                .inspectionScore(entity.getInspectionScore())
                .carDetails(entity.getCarDetails())
                .price(entity.getPrice())
                .imported(entity.isImported())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
