package com.venuecrm.app.service.impl;

import com.venuecrm.app.model.entity.Hall;
import com.venuecrm.app.repository.HallRepository;
import com.venuecrm.app.service.CapacityRangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapacityRangeServiceImpl implements CapacityRangeService {

    private final HallRepository hallRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GuestRangeOption> computeRanges(UUID tenantId) {
        List<Hall> halls = hallRepository.findByTenantIdAndActiveTrueOrderBySortOrderAscNameAsc(tenantId).stream()
                .filter(h -> h.getCapacityMin() != null || h.getCapacityMax() != null)
                .toList();
        if (halls.isEmpty()) return List.of();

        TreeSet<Integer> breakpoints = new TreeSet<>();
        boolean hasUnbounded = false;
        for (Hall h : halls) {
            breakpoints.add(h.getCapacityMin() != null ? h.getCapacityMin() : 0);
            if (h.getCapacityMax() != null) {
                breakpoints.add(h.getCapacityMax());
            } else {
                hasUnbounded = true;
            }
        }

        List<Integer> points = new ArrayList<>(breakpoints);
        List<GuestRangeOption> raw = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            addIfCovered(raw, halls, points.get(i), points.get(i + 1));
        }
        if (hasUnbounded && !points.isEmpty()) {
            addIfCovered(raw, halls, points.get(points.size() - 1), null);
        }

        return merge(raw);
    }

    private void addIfCovered(List<GuestRangeOption> raw, List<Hall> halls, int start, Integer end) {
        List<Hall> matching = halls.stream().filter(h -> hallCovers(h, start, end)).toList();
        if (!matching.isEmpty()) {
            raw.add(new GuestRangeOption(start, end, matching));
        }
    }

    private boolean hallCovers(Hall h, int segStart, Integer segEnd) {
        int hallMin = h.getCapacityMin() != null ? h.getCapacityMin() : 0;
        if (hallMin > segStart) return false;
        if (segEnd == null) {
            return h.getCapacityMax() == null;
        }
        return h.getCapacityMax() == null || h.getCapacityMax() >= segEnd;
    }

    /** Aynı salon kümesine sahip komşu alt aralıkları tek bir seçenekte birleştirir. */
    private List<GuestRangeOption> merge(List<GuestRangeOption> raw) {
        List<GuestRangeOption> merged = new ArrayList<>();
        for (GuestRangeOption opt : raw) {
            GuestRangeOption last = merged.isEmpty() ? null : merged.get(merged.size() - 1);
            if (last != null && last.end() != null && last.end().equals(opt.start()) && sameHalls(last.halls(), opt.halls())) {
                merged.set(merged.size() - 1, new GuestRangeOption(last.start(), opt.end(), last.halls()));
            } else {
                merged.add(opt);
            }
        }
        return merged;
    }

    private boolean sameHalls(List<Hall> a, List<Hall> b) {
        if (a.size() != b.size()) return false;
        Set<UUID> aIds = a.stream().map(Hall::getId).collect(Collectors.toSet());
        return b.stream().allMatch(h -> aIds.contains(h.getId()));
    }
}
