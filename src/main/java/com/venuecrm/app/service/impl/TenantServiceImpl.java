package com.venuecrm.app.service.impl;

import com.venuecrm.app.exception.runtime.TenantNotFoundException;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.request.TenantSettingsUpdateRequest;
import com.venuecrm.app.model.response.TenantResponse;
import com.venuecrm.app.repository.TenantRepository;
import com.venuecrm.app.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> findByPhoneNumberId(String phoneNumberId) {
        return tenantRepository.findByWaPhoneNumberId(phoneNumberId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSubscriptionActive(Tenant tenant) {
        if ("CANCELLED".equals(tenant.getSubscriptionStatus())) return false;
        LocalDate until = tenant.getSubscriptionUntil();
        if (until == null) return true;
        return !LocalDate.now().isAfter(until.plusDays(7));
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse getSettings(UUID tenantId) {
        return toResponse(tenantRepository.findById(tenantId).orElseThrow(TenantNotFoundException::new));
    }

    @Override
    @Transactional
    public TenantResponse updateSettings(UUID tenantId, TenantSettingsUpdateRequest req) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(TenantNotFoundException::new);
        if (req.displayName() != null && !req.displayName().isBlank()) tenant.setDisplayName(req.displayName().trim());
        if (req.greetingText() != null) tenant.setGreetingText(req.greetingText());
        if (req.showPrices() != null) tenant.setShowPrices(req.showPrices());
        if (req.waPhoneNumberId() != null && !req.waPhoneNumberId().isBlank()) tenant.setWaPhoneNumberId(req.waPhoneNumberId().trim());
        if (req.waDisplayNumber() != null && !req.waDisplayNumber().isBlank()) tenant.setWaDisplayNumber(req.waDisplayNumber().trim());
        if (req.waAccessToken() != null && !req.waAccessToken().isBlank()) tenant.setWaAccessToken(req.waAccessToken().trim());
        return toResponse(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantResponse> getAll() {
        return tenantRepository.findAllByOrderByDisplayNameAsc().stream().map(this::toResponse).toList();
    }

    private TenantResponse toResponse(Tenant t) {
        return new TenantResponse(
                t.getId().toString(), t.getSlug(), t.getDisplayName(), t.getVertical(),
                t.getWaPhoneNumberId(), t.getWaDisplayNumber(), t.getTimezone(), t.getGreetingText(),
                t.isActive(), t.getSubscriptionStatus(), t.getSubscriptionUntil(), t.getPlan(), t.isShowPrices());
    }
}
