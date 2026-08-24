package com.venuecrm.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "push")
public record PushProperties(String publicKey, String privateKey, String subject) {}
