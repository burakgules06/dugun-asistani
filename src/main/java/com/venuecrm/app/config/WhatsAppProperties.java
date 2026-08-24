package com.venuecrm.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "whatsapp")
public record WhatsAppProperties(Api api, Webhook webhook) {
    public record Api(String baseUrl, String accessToken) {}
    public record Webhook(String verifyToken) {}
}
