package com.venuecrm.app.controller;

import com.venuecrm.app.config.PushProperties;
import com.venuecrm.app.model.request.PushSubscribeRequest;
import com.venuecrm.app.security.PanelPrincipal;
import com.venuecrm.app.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushSubscriptionController {

    private final PushNotificationService pushNotificationService;
    private final PushProperties pushProperties;

    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> publicKey() {
        return ResponseEntity.ok(Map.of("publicKey", pushProperties.publicKey() != null ? pushProperties.publicKey() : ""));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestBody PushSubscribeRequest req) {
        pushNotificationService.subscribe(principal.userId(), req);
        return ResponseEntity.ok().build();
    }

    public record UnsubscribeRequest(String endpoint) {}

    @DeleteMapping("/subscribe")
    public ResponseEntity<Void> unsubscribe(@RequestBody UnsubscribeRequest req) {
        pushNotificationService.unsubscribe(req.endpoint());
        return ResponseEntity.ok().build();
    }
}
