package com.venuecrm.app.controller;

import com.venuecrm.app.model.request.MenuUpsertRequest;
import com.venuecrm.app.model.response.MenuResponse;
import com.venuecrm.app.security.PanelPrincipal;
import com.venuecrm.app.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuResponse>> list(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        return ResponseEntity.ok(menuService.getAll(principal.tenantId(), includeInactive));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuResponse> create(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestBody MenuUpsertRequest req) {
        return ResponseEntity.ok(menuService.create(principal.tenantId(), req));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuResponse> update(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID id,
            @RequestBody MenuUpsertRequest req) {
        return ResponseEntity.ok(menuService.update(principal.tenantId(), id, req));
    }

    @PatchMapping("/set-active/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuResponse> setActive(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(menuService.setActive(principal.tenantId(), id, Boolean.TRUE.equals(body.get("active"))));
    }
}
