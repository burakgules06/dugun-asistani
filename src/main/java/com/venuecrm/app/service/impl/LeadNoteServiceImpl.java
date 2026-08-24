package com.venuecrm.app.service.impl;

import com.venuecrm.app.exception.runtime.LeadNotBelongToTenantException;
import com.venuecrm.app.exception.runtime.LeadNotFoundException;
import com.venuecrm.app.model.entity.Lead;
import com.venuecrm.app.model.entity.LeadNote;
import com.venuecrm.app.model.entity.PanelUser;
import com.venuecrm.app.model.request.LeadNoteRequest;
import com.venuecrm.app.model.response.LeadNoteResponse;
import com.venuecrm.app.repository.LeadNoteRepository;
import com.venuecrm.app.repository.LeadRepository;
import com.venuecrm.app.repository.PanelUserRepository;
import com.venuecrm.app.service.LeadNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadNoteServiceImpl implements LeadNoteService {

    private final LeadNoteRepository leadNoteRepository;
    private final LeadRepository leadRepository;
    private final PanelUserRepository panelUserRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LeadNoteResponse> list(UUID tenantId, UUID leadId) {
        loadOwned(tenantId, leadId);
        return leadNoteRepository.findByLead_IdOrderByCreatedAtDesc(leadId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public LeadNoteResponse add(UUID tenantId, UUID leadId, UUID authorUserId, LeadNoteRequest req) {
        if (req.body() == null || req.body().isBlank()) {
            throw new IllegalArgumentException("Not boş olamaz");
        }
        Lead lead = loadOwned(tenantId, leadId);

        LeadNote note = new LeadNote();
        note.setLead(lead);
        if (authorUserId != null) {
            PanelUser author = panelUserRepository.findById(authorUserId).orElse(null);
            note.setAuthor(author);
        }
        note.setBody(req.body().trim());
        // saveAndFlush: @CreationTimestamp yalnizca flush aninda atanir - flush edilmezse
        // bu istegin doneceği DTO'da createdAt hala null gorunur.
        return toResponse(leadNoteRepository.saveAndFlush(note));
    }

    @Override
    @Transactional
    public void addSystemNote(UUID leadId, String body) {
        Lead lead = leadRepository.findById(leadId).orElseThrow(LeadNotFoundException::new);
        LeadNote note = new LeadNote();
        note.setLead(lead);
        note.setBody(body);
        leadNoteRepository.save(note);
    }

    private Lead loadOwned(UUID tenantId, UUID leadId) {
        Lead lead = leadRepository.findById(leadId).orElseThrow(LeadNotFoundException::new);
        if (!lead.getTenant().getId().equals(tenantId)) {
            throw new LeadNotBelongToTenantException();
        }
        return lead;
    }

    private LeadNoteResponse toResponse(LeadNote n) {
        return new LeadNoteResponse(
                n.getId().toString(),
                n.getAuthor() != null ? n.getAuthor().getFullName() : "Sistem",
                n.getBody(),
                n.getCreatedAt());
    }
}
