package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findByTenantIdAndActiveTrueOrderBySortOrderAscNameAsc(UUID tenantId);
    List<Menu> findByTenantIdOrderBySortOrderAscNameAsc(UUID tenantId);
}
