package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HallRepository extends JpaRepository<Hall, UUID> {
    List<Hall> findByTenantIdAndActiveTrueOrderBySortOrderAscNameAsc(UUID tenantId);
    List<Hall> findByTenantIdOrderBySortOrderAscNameAsc(UUID tenantId);
}
