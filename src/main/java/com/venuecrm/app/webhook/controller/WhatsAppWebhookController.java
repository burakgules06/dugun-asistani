package com.venuecrm.app.webhook.controller;

import com.venuecrm.app.config.WhatsAppProperties;
import com.venuecrm.app.service.InboundMessageService;
import com.venuecrm.app.webhook.dto.WebhookPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WhatsAppWebhookController {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppWebhookController.class);
    private final WhatsAppProperties props;
    private final InboundMessageService inboundMessageService;

    @GetMapping
    public ResponseEntity<String> verify(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && props.webhook().verifyToken().equals(token)) {
            log.info("Webhook dogrulamasi BASARILI");
            return ResponseEntity.ok(challenge);
        }
        log.warn("Webhook dogrulamasi REDDEDILDI");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody WebhookPayload payload) {
        inboundMessageService.process(payload);
        return ResponseEntity.ok().build();
    }
}
