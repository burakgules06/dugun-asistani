package com.venuecrm.app.service;

import com.venuecrm.app.model.request.LeadNoteRequest;
import com.venuecrm.app.model.response.LeadNoteResponse;

import java.util.List;
import java.util.UUID;

public interface LeadNoteService {
    List<LeadNoteResponse> list(UUID tenantId, UUID leadId);
    LeadNoteResponse add(UUID tenantId, UUID leadId, UUID authorUserId, LeadNoteRequest req);
    void addSystemNote(UUID leadId, String body);
}
