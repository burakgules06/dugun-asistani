package com.venuecrm.app.controller;

import com.venuecrm.app.model.request.LeadNoteRequest;
import com.venuecrm.app.model.response.LeadNoteResponse;
import com.venuecrm.app.security.PanelPrincipal;
import com.venuecrm.app.service.LeadNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lead/{leadId}/note")
@RequiredArgsConstructor
public class LeadNoteController {

    private final LeadNoteService leadNoteService;

    @GetMapping
    public ResponseEntity<List<LeadNoteResponse>> list(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID leadId) {
        return ResponseEntity.ok(leadNoteService.list(principal.tenantId(), leadId));
    }

    @PostMapping
    public ResponseEntity<LeadNoteResponse> add(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID leadId,
            @RequestBody LeadNoteRequest req) {
        return ResponseEntity.ok(leadNoteService.add(principal.tenantId(), leadId, principal.userId(), req));
    }
}
