package com.venuecrm.app.model.request;

public record TenantSettingsUpdateRequest(
        String displayName,
        String greetingText,
        Boolean showPrices,
        String waPhoneNumberId,
        String waDisplayNumber,
        String waAccessToken
) {}
