package com.venuecrm.app.controller;

import com.venuecrm.app.model.request.ChangePasswordRequest;
import com.venuecrm.app.model.request.PanelUserUpsertRequest;
import com.venuecrm.app.model.response.PanelUserResponse;
import com.venuecrm.app.security.PanelPrincipal;
import com.venuecrm.app.service.PanelUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final PanelUserService panelUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PanelUserResponse>> list(@AuthenticationPrincipal PanelPrincipal principal) {
        return ResponseEntity.ok(panelUserService.getAll(principal.tenantId()));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PanelUserResponse> create(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestBody PanelUserUpsertRequest req) {
        return ResponseEntity.ok(panelUserService.create(principal.tenantId(), req));
    }

    @PatchMapping("/set-active/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PanelUserResponse> setActive(
            @AuthenticationPrincipal PanelPrincipal principal,
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(panelUserService.setActive(principal.tenantId(), id, Boolean.TRUE.equals(body.get("active"))));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestBody ChangePasswordRequest req) {
        panelUserService.changePassword(principal.userId(), req);
        return ResponseEntity.ok().build();
    }
}
