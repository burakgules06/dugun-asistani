package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.PanelUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PanelUserRepository extends JpaRepository<PanelUser, UUID> {
    Optional<PanelUser> findByPhoneAndActiveTrue(String phone);
    boolean existsByPhone(String phone);
    List<PanelUser> findByTenantIdOrderByFullNameAsc(UUID tenantId);
}
