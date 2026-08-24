package com.venuecrm.app.service.impl;

import com.venuecrm.app.exception.runtime.CapacityRuleNotBelongToTenantException;
import com.venuecrm.app.exception.runtime.CapacityRuleNotFoundException;
import com.venuecrm.app.exception.runtime.HallNotFoundException;
import com.venuecrm.app.exception.runtime.MenuNotFoundException;
import com.venuecrm.app.model.entity.CapacityRule;
import com.venuecrm.app.model.entity.Hall;
import com.venuecrm.app.model.entity.Menu;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.enums.TimeSlot;
import com.venuecrm.app.model.request.CapacityRuleUpsertRequest;
import com.venuecrm.app.model.response.CapacityRuleResponse;
import com.venuecrm.app.repository.CapacityRuleRepository;
import com.venuecrm.app.repository.HallRepository;
import com.venuecrm.app.repository.MenuRepository;
import com.venuecrm.app.repository.TenantRepository;
import com.venuecrm.app.service.CapacityRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CapacityRuleServiceImpl implements CapacityRuleService {

    private final CapacityRuleRepository capacityRuleRepository;
    private final TenantRepository tenantRepository;
    private final HallRepository hallRepository;
    private final MenuRepository menuRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CapacityRuleResponse> getAll(UUID tenantId) {
        return capacityRuleRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public CapacityRuleResponse create(UUID tenantId, CapacityRuleUpsertRequest req) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();

        CapacityRule r = new CapacityRule();
        r.setTenant(tenant);
        applyRequest(tenantId, r, req);
        return toResponse(capacityRuleRepository.save(r));
    }

    @Override
    @Transactional
    public CapacityRuleResponse update(UUID tenantId, UUID ruleId, CapacityRuleUpsertRequest req) {
        CapacityRule r = loadOwned(tenantId, ruleId);
        applyRequest(tenantId, r, req);
        return toResponse(r);
    }

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID ruleId) {
        CapacityRule r = loadOwned(tenantId, ruleId);
        capacityRuleRepository.delete(r);
    }

    private void applyRequest(UUID tenantId, CapacityRule r, CapacityRuleUpsertRequest req) {
        if (req.hallId() != null) {
            Hall hall = hallRepository.findById(req.hallId()).orElseThrow(HallNotFoundException::new);
            r.setHall(hall);
        } else {
            r.setHall(null);
        }
        if (req.menuId() != null) {
            Menu menu = menuRepository.findById(req.menuId()).orElseThrow(MenuNotFoundException::new);
            r.setMenu(menu);
        } else {
            r.setMenu(null);
        }
        if (req.months() != null && !req.months().isEmpty()) {
            short[] arr = new short[req.months().size()];
            for (int i = 0; i < arr.length; i++) arr[i] = req.months().get(i).shortValue();
            r.setMonths(arr);
        } else {
            r.setMonths(null);
        }
        r.setTimeSlot(req.timeSlot() != null && !req.timeSlot().isBlank() ? TimeSlot.valueOf(req.timeSlot()) : null);
        r.setActive(req.active() == null || req.active());
        r.setNote(req.note());
    }

    private CapacityRule loadOwned(UUID tenantId, UUID ruleId) {
        CapacityRule r = capacityRuleRepository.findById(ruleId).orElseThrow(CapacityRuleNotFoundException::new);
        if (!r.getTenant().getId().equals(tenantId)) {
            throw new CapacityRuleNotBelongToTenantException();
        }
        return r;
    }

    private CapacityRuleResponse toResponse(CapacityRule r) {
        List<Integer> months = new ArrayList<>();
        if (r.getMonths() != null) {
            for (short m : r.getMonths()) months.add((int) m);
        }
        return new CapacityRuleResponse(
                r.getId().toString(),
                r.getHall() != null ? r.getHall().getId().toString() : null,
                r.getHall() != null ? r.getHall().getName() : null,
                r.getMenu() != null ? r.getMenu().getId().toString() : null,
                r.getMenu() != null ? r.getMenu().getName() : null,
                months,
                r.getTimeSlot() != null ? r.getTimeSlot().name() : null,
                r.isActive(),
                r.getNote()
        );
    }
}
