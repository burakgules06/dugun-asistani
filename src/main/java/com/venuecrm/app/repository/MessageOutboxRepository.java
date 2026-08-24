package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.MessageOutbox;
import com.venuecrm.app.model.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface MessageOutboxRepository extends JpaRepository<MessageOutbox, Long> {
    List<MessageOutbox> findByStatusAndScheduledForBefore(OutboxStatus status, OffsetDateTime before);
}
