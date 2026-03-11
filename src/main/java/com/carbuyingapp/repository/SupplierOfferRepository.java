package com.carbuyingapp.repository;

import com.carbuyingapp.model.entity.SupplierOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierOfferRepository extends JpaRepository<SupplierOffer, Long> {

    Page<SupplierOffer> findByRequestId(Long requestId, Pageable pageable);

    Page<SupplierOffer> findBySupplierId(Long supplierId, Pageable pageable);

    boolean existsBySupplierIdAndRequestId(Long supplierId, Long requestId);

    long countByRequestId(Long requestId);
}
