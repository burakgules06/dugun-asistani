package com.venuecrm.app.security;

import java.util.UUID;

public record PanelPrincipal(UUID userId, UUID tenantId, String role) {}
