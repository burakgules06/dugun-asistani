package com.venuecrm.app.controller;

import com.venuecrm.app.model.request.TenantSettingsUpdateRequest;
import com.venuecrm.app.model.response.TenantResponse;
import com.venuecrm.app.security.PanelPrincipal;
import com.venuecrm.app.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final TenantService tenantService;

    @GetMapping
    public ResponseEntity<TenantResponse> get(@AuthenticationPrincipal PanelPrincipal principal) {
        return ResponseEntity.ok(tenantService.getSettings(principal.tenantId()));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponse> update(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestBody TenantSettingsUpdateRequest req) {
        return ResponseEntity.ok(tenantService.updateSettings(principal.tenantId(), req));
    }
}
