package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.CapacityRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CapacityRuleRepository extends JpaRepository<CapacityRule, UUID> {
    List<CapacityRule> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    List<CapacityRule> findByTenantIdAndActiveTrue(UUID tenantId);
}
