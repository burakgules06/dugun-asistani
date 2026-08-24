package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    List<Lead> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    List<Lead> findByTenantIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID tenantId, OffsetDateTime after);
}
