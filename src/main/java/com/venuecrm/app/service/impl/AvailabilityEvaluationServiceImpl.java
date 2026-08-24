package com.venuecrm.app.service.impl;

import com.venuecrm.app.model.entity.CapacityRule;
import com.venuecrm.app.model.enums.TimeSlot;
import com.venuecrm.app.repository.CapacityRuleRepository;
import com.venuecrm.app.service.AvailabilityEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Panelden tanımlı istisnaları (CapacityRule) değerlendirir: bir salon/menü/ay/zaman-dilimi
 * kombinasyonunu kapsayan AKTİF bir istisna varsa o kombinasyon kapalı sayılır.
 */
@Service
@RequiredArgsConstructor
public class AvailabilityEvaluationServiceImpl implements AvailabilityEvaluationService {

    private final CapacityRuleRepository capacityRuleRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isClosed(UUID tenantId, UUID hallId, UUID menuId, Integer month, TimeSlot timeSlot) {
        if (month == null) return false;

        for (CapacityRule rule : capacityRuleRepository.findByTenantIdAndActiveTrue(tenantId)) {
            if (!scopeMatches(rule.getHall() != null ? rule.getHall().getId() : null, hallId)) continue;
            if (!scopeMatches(rule.getMenu() != null ? rule.getMenu().getId() : null, menuId)) continue;
            if (!timeSlotMatches(rule.getTimeSlot(), timeSlot)) continue;
            if (!monthMatches(rule.getMonths(), month)) continue;

            return true;
        }
        return false;
    }

    /** Kural kapsamı null ise ("tüm salonlar/menüler") her zaman eşleşir; dolu ise talep edilenle aynı olmalı. */
    private boolean scopeMatches(UUID ruleScopeId, UUID requestedId) {
        if (ruleScopeId == null) return true;
        return ruleScopeId.equals(requestedId);
    }

    /**
     * Henüz zaman dilimi secilmemisse (requestedTimeSlot=null, menü listesi filtrelenirken)
     * yanlis-pozitif (gereksiz kapama) uretmemek adina yalnizca tum dilimleri kapsayan
     * (ruleTimeSlot=null) kurallar kontrol edilir. Belirli bir dilim istenirse o dilime
     * ya da tum dilimlere kapsayan kurallar kontrol edilir.
     */
    private boolean timeSlotMatches(TimeSlot ruleTimeSlot, TimeSlot requestedTimeSlot) {
        if (requestedTimeSlot == null) {
            return ruleTimeSlot == null;
        }
        return ruleTimeSlot == null || ruleTimeSlot == requestedTimeSlot;
    }

    private boolean monthMatches(short[] months, int month) {
        if (months == null || months.length == 0) return true;
        for (short m : months) {
            if (m == month) return true;
        }
        return false;
    }
}
