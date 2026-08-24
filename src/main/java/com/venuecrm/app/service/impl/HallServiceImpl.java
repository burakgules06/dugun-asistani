package com.venuecrm.app.service.impl;

import com.venuecrm.app.exception.runtime.HallNotBelongToTenantException;
import com.venuecrm.app.exception.runtime.HallNotFoundException;
import com.venuecrm.app.model.entity.Hall;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.request.HallUpsertRequest;
import com.venuecrm.app.model.response.HallResponse;
import com.venuecrm.app.repository.HallRepository;
import com.venuecrm.app.repository.TenantRepository;
import com.venuecrm.app.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HallResponse> getAll(UUID tenantId, boolean includeInactive) {
        var halls = includeInactive
                ? hallRepository.findByTenantIdOrderBySortOrderAscNameAsc(tenantId)
                : hallRepository.findByTenantIdAndActiveTrueOrderBySortOrderAscNameAsc(tenantId);
        return halls.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public HallResponse create(UUID tenantId, HallUpsertRequest req) {
        validate(req);
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();

        Hall h = new Hall();
        h.setTenant(tenant);
        applyRequest(h, req);
        h.setActive(true);

        return toResponse(hallRepository.save(h));
    }

    @Override
    @Transactional
    public HallResponse update(UUID tenantId, UUID hallId, HallUpsertRequest req) {
        validate(req);
        Hall h = loadOwned(tenantId, hallId);
        applyRequest(h, req);
        return toResponse(h);
    }

    @Override
    @Transactional
    public HallResponse setActive(UUID tenantId, UUID hallId, boolean active) {
        Hall h = loadOwned(tenantId, hallId);
        h.setActive(active);
        return toResponse(h);
    }

    @Override
    @Transactional
    public void reorder(UUID tenantId, List<UUID> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()) return;
        List<Hall> halls = orderedIds.stream().map(id -> loadOwned(tenantId, id)).toList();
        for (int i = 0; i < halls.size(); i++) {
            halls.get(i).setSortOrder((short) i);
        }
    }

    private void applyRequest(Hall h, HallUpsertRequest req) {
        h.setName(req.name().trim());
        h.setDescription(req.description());
        h.setCapacityMin(req.capacityMin());
        h.setCapacityMax(req.capacityMax());
        if (req.sortOrder() != null) h.setSortOrder(req.sortOrder().shortValue());
        h.setDailyCapacity(req.dailyCapacity() != null ? req.dailyCapacity().shortValue() : (short) 1);
    }

    private Hall loadOwned(UUID tenantId, UUID hallId) {
        Hall h = hallRepository.findById(hallId).orElseThrow(HallNotFoundException::new);
        if (!h.getTenant().getId().equals(tenantId)) {
            throw new HallNotBelongToTenantException();
        }
        return h;
    }

    private void validate(HallUpsertRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("Salon adı boş olamaz");
        }
        if (req.dailyCapacity() != null && req.dailyCapacity() != 1 && req.dailyCapacity() != 2) {
            throw new IllegalArgumentException("Günlük kapasite 1 veya 2 olmalı");
        }
    }

    private HallResponse toResponse(Hall h) {
        return new HallResponse(
                h.getId().toString(), h.getName(), h.getDescription(),
                h.getCapacityMin(), h.getCapacityMax(), h.getSortOrder(), h.isActive(), h.getDailyCapacity());
    }
}
