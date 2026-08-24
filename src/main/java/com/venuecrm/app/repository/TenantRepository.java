package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByWaPhoneNumberId(String waPhoneNumberId);
    List<Tenant> findAllByOrderByDisplayNameAsc();
}
