package com.venuecrm.app.controller;

import com.venuecrm.app.model.request.HallUpsertRequest;
import com.venuecrm.app.model.response.HallResponse;
import com.venuecrm.app.security.PanelPrincipal;
import com.venuecrm.app.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/hall")
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;

    @GetMapping
    public ResponseEntity<List<HallResponse>> list(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        return ResponseEntity.ok(hallService.getAll(principal.tenantId(), includeInactive));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HallResponse> create(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestBody HallUpsertRequest req) {
        return ResponseEntity.ok(hallService.create(principal.tenantId(), req));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HallResponse> update(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID id,
            @RequestBody HallUpsertRequest req) {
        return ResponseEntity.ok(hallService.update(principal.tenantId(), id, req));
    }

    @PatchMapping("/set-active/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HallResponse> setActive(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(hallService.setActive(principal.tenantId(), id, Boolean.TRUE.equals(body.get("active"))));
    }

    @PatchMapping("/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reorder(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestBody ReorderRequest req) {
        List<UUID> ids = (req.ids() == null) ? List.of() : req.ids().stream().map(UUID::fromString).toList();
        hallService.reorder(principal.tenantId(), ids);
        return ResponseEntity.ok().build();
    }

    public record ReorderRequest(List<String> ids) {}
}
