package com.carbuyingapp.repository;

import com.carbuyingapp.model.entity.CustomerRequest;
import com.carbuyingapp.model.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {

    Page<CustomerRequest> findByStatus(RequestStatus status, Pageable pageable);

    @Query("""
            SELECT cr, COUNT(so.id) FROM CustomerRequest cr
            LEFT JOIN SupplierOffer so ON so.request.id = cr.id
            GROUP BY cr
            """)
    Page<Object[]> findAllWithOfferCount(Pageable pageable);

    @Query("""
            SELECT cr, COUNT(so.id) FROM CustomerRequest cr
            LEFT JOIN SupplierOffer so ON so.request.id = cr.id
            WHERE cr.status = :status
            GROUP BY cr
            """)
    Page<Object[]> findByStatusWithOfferCount(RequestStatus status, Pageable pageable);
}
