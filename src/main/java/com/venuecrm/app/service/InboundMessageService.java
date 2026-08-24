package com.venuecrm.app.service;

import com.venuecrm.app.webhook.dto.WebhookPayload;

public interface InboundMessageService {
    void process(WebhookPayload payload);
}
