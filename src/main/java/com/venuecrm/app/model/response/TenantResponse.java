package com.venuecrm.app.model.response;

import java.time.LocalDate;

public record TenantResponse(
        String id,
        String slug,
        String displayName,
        String vertical,
        String waPhoneNumberId,
        String waDisplayNumber,
        String timezone,
        String greetingText,
        boolean active,
        String subscriptionStatus,
        LocalDate subscriptionUntil,
        String plan,
        boolean showPrices
) {}
