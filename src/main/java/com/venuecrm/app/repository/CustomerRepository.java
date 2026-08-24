package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByTenantIdAndWaNumber(UUID tenantId, String waNumber);
    Optional<Customer> findByTenantIdAndWaUserId(UUID tenantId, String waUserId);

    @Query("""
            SELECT c FROM Customer c
            WHERE c.tenant.id = :tenantId
              AND (LOWER(c.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR c.waNumber LIKE CONCAT('%', :query, '%'))
            ORDER BY c.fullName ASC
            """)
    List<Customer> search(@Param("tenantId") UUID tenantId, @Param("query") String query, Pageable pageable);
}
