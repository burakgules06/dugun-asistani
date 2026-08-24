package com.venuecrm.app.controller;

import com.venuecrm.app.model.request.CapacityRuleUpsertRequest;
import com.venuecrm.app.model.response.CapacityRuleResponse;
import com.venuecrm.app.security.PanelPrincipal;
import com.venuecrm.app.service.CapacityRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/capacity-rule")
@RequiredArgsConstructor
public class CapacityRuleController {

    private final CapacityRuleService capacityRuleService;

    @GetMapping
    public ResponseEntity<List<CapacityRuleResponse>> list(@AuthenticationPrincipal PanelPrincipal principal) {
        return ResponseEntity.ok(capacityRuleService.getAll(principal.tenantId()));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CapacityRuleResponse> create(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestBody CapacityRuleUpsertRequest req) {
        return ResponseEntity.ok(capacityRuleService.create(principal.tenantId(), req));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CapacityRuleResponse> update(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID id,
            @RequestBody CapacityRuleUpsertRequest req) {
        return ResponseEntity.ok(capacityRuleService.update(principal.tenantId(), id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID id) {
        capacityRuleService.delete(principal.tenantId(), id);
        return ResponseEntity.ok().build();
    }
}
