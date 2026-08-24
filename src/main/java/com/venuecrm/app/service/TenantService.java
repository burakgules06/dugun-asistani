package com.venuecrm.app.service;

import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.request.TenantSettingsUpdateRequest;
import com.venuecrm.app.model.response.TenantResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantService {
    Optional<Tenant> findByPhoneNumberId(String phoneNumberId);
    boolean isSubscriptionActive(Tenant tenant);
    TenantResponse getSettings(UUID tenantId);
    TenantResponse updateSettings(UUID tenantId, TenantSettingsUpdateRequest req);
    List<TenantResponse> getAll();
}
