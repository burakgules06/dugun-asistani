package com.venuecrm.app.service.impl;

import com.venuecrm.app.exception.runtime.HallNotFoundException;
import com.venuecrm.app.exception.runtime.MenuNotBelongToTenantException;
import com.venuecrm.app.exception.runtime.MenuNotFoundException;
import com.venuecrm.app.model.entity.Hall;
import com.venuecrm.app.model.entity.HallMenu;
import com.venuecrm.app.model.entity.Menu;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.request.MenuUpsertRequest;
import com.venuecrm.app.model.response.MenuResponse;
import com.venuecrm.app.repository.HallMenuRepository;
import com.venuecrm.app.repository.HallRepository;
import com.venuecrm.app.repository.MenuRepository;
import com.venuecrm.app.repository.TenantRepository;
import com.venuecrm.app.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final TenantRepository tenantRepository;
    private final HallRepository hallRepository;
    private final HallMenuRepository hallMenuRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> getAll(UUID tenantId, boolean includeInactive) {
        var menus = includeInactive
                ? menuRepository.findByTenantIdOrderBySortOrderAscNameAsc(tenantId)
                : menuRepository.findByTenantIdAndActiveTrueOrderBySortOrderAscNameAsc(tenantId);
        return menus.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public MenuResponse create(UUID tenantId, MenuUpsertRequest req) {
        validate(req);
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();

        Menu m = new Menu();
        m.setTenant(tenant);
        applyRequest(m, req);
        m.setActive(true);
        Menu saved = menuRepository.save(m);

        syncHalls(tenantId, saved, req.hallIds());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public MenuResponse update(UUID tenantId, UUID menuId, MenuUpsertRequest req) {
        validate(req);
        Menu m = loadOwned(tenantId, menuId);
        applyRequest(m, req);
        syncHalls(tenantId, m, req.hallIds());
        return toResponse(m);
    }

    @Override
    @Transactional
    public MenuResponse setActive(UUID tenantId, UUID menuId, boolean active) {
        Menu m = loadOwned(tenantId, menuId);
        m.setActive(active);
        return toResponse(m);
    }

    private void syncHalls(UUID tenantId, Menu menu, List<UUID> hallIds) {
        hallMenuRepository.deleteByMenu_Id(menu.getId());
        if (hallIds == null || hallIds.isEmpty()) return;
        for (UUID hallId : hallIds) {
            Hall hall = hallRepository.findById(hallId).orElseThrow(HallNotFoundException::new);
            if (!hall.getTenant().getId().equals(tenantId)) continue;
            HallMenu hm = new HallMenu();
            hm.setHall(hall);
            hm.setMenu(menu);
            hallMenuRepository.save(hm);
        }
    }

    private void applyRequest(Menu m, MenuUpsertRequest req) {
        m.setName(req.name().trim());
        m.setDescription(req.description());
        m.setPricePerPerson(req.pricePerPerson());
        if (req.sortOrder() != null) m.setSortOrder(req.sortOrder().shortValue());
    }

    private Menu loadOwned(UUID tenantId, UUID menuId) {
        Menu m = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);
        if (!m.getTenant().getId().equals(tenantId)) {
            throw new MenuNotBelongToTenantException();
        }
        return m;
    }

    private void validate(MenuUpsertRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("Menü adı boş olamaz");
        }
    }

    private MenuResponse toResponse(Menu m) {
        List<String> hallIds = hallMenuRepository.findByMenu_Id(m.getId()).stream()
                .map(hm -> hm.getHall().getId().toString())
                .toList();
        return new MenuResponse(
                m.getId().toString(), m.getName(), m.getDescription(),
                m.getPricePerPerson(), m.getSortOrder(), m.isActive(), hallIds);
    }
}
