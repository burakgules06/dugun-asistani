package com.venuecrm.app.repository;

import com.venuecrm.app.model.entity.LeadNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeadNoteRepository extends JpaRepository<LeadNote, UUID> {
    List<LeadNote> findByLead_IdOrderByCreatedAtDesc(UUID leadId);
}
