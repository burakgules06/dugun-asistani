package com.venuecrm.app.service.impl;

import com.venuecrm.app.exception.runtime.PanelUserPhoneAlreadyExistException;
import com.venuecrm.app.exception.runtime.TenantNotFoundException;
import com.venuecrm.app.model.entity.PanelUser;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.request.ChangePasswordRequest;
import com.venuecrm.app.model.request.PanelUserUpsertRequest;
import com.venuecrm.app.model.response.PanelUserResponse;
import com.venuecrm.app.repository.PanelUserRepository;
import com.venuecrm.app.repository.TenantRepository;
import com.venuecrm.app.service.PanelUserService;
import com.venuecrm.app.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PanelUserServiceImpl implements PanelUserService {

    private final PanelUserRepository panelUserRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<PanelUserResponse> getAll(UUID tenantId) {
        return panelUserRepository.findByTenantIdOrderByFullNameAsc(tenantId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public PanelUserResponse create(UUID tenantId, PanelUserUpsertRequest req) {
        String normalized = PhoneUtil.normalize(req.phone());
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException("Geçerli bir telefon numarası girin");
        }
        if (panelUserRepository.existsByPhone(normalized)) {
            throw new PanelUserPhoneAlreadyExistException();
        }
        if (req.password() == null || req.password().length() < 6) {
            throw new IllegalArgumentException("Şifre en az 6 karakter olmalı");
        }
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(TenantNotFoundException::new);

        PanelUser u = new PanelUser();
        u.setTenant(tenant);
        u.setPhone(normalized);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setFullName(req.fullName());
        u.setRole(req.role() != null ? req.role() : "STAFF");
        u.setActive(true);
        return toResponse(panelUserRepository.save(u));
    }

    @Override
    @Transactional
    public PanelUserResponse setActive(UUID tenantId, UUID userId, boolean active) {
        PanelUser u = panelUserRepository.findById(userId).orElseThrow();
        if (u.getTenant() == null || !u.getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Kullanıcı bu işletmeye ait değil");
        }
        u.setActive(active);
        return toResponse(u);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest req) {
        PanelUser u = panelUserRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(req.currentPassword(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Mevcut şifre hatalı");
        }
        if (req.newPassword() == null || req.newPassword().length() < 6) {
            throw new IllegalArgumentException("Yeni şifre en az 6 karakter olmalı");
        }
        u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
    }

    private PanelUserResponse toResponse(PanelUser u) {
        return new PanelUserResponse(u.getId().toString(), u.getPhone(), u.getFullName(), u.getRole(), u.isActive());
    }
}
