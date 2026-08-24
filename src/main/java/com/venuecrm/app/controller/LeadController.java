package com.venuecrm.app.controller;

import com.venuecrm.app.model.request.LeadCreateRequest;
import com.venuecrm.app.model.request.LeadUpdateRequest;
import com.venuecrm.app.model.response.LeadResponse;
import com.venuecrm.app.security.PanelPrincipal;
import com.venuecrm.app.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lead")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    public ResponseEntity<List<LeadResponse>> list(@AuthenticationPrincipal PanelPrincipal principal) {
        return ResponseEntity.ok(leadService.getAll(principal.tenantId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> get(@AuthenticationPrincipal PanelPrincipal principal, @PathVariable UUID id) {
        return ResponseEntity.ok(leadService.getById(principal.tenantId(), id));
    }

    @PostMapping("/create")
    public ResponseEntity<LeadResponse> create(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestBody LeadCreateRequest req) {
        return ResponseEntity.ok(leadService.create(principal.tenantId(), req));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LeadResponse> update(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID id,
            @RequestBody LeadUpdateRequest req) {
        return ResponseEntity.ok(leadService.update(principal.tenantId(), id, req));
    }
}
