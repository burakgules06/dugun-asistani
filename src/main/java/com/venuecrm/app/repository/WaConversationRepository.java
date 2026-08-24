package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.WaConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WaConversationRepository extends JpaRepository<WaConversation, UUID> {
    Optional<WaConversation> findByTenantIdAndCustomerId(UUID tenantId, UUID customerId);
}
