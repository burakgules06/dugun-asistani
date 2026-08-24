package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.WaMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaMessageLogRepository extends JpaRepository<WaMessageLog, Long> {
    boolean existsByWaMessageId(String waMessageId);
}
