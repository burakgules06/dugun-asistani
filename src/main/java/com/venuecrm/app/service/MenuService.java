package com.venuecrm.app.service;

import com.venuecrm.app.model.request.MenuUpsertRequest;
import com.venuecrm.app.model.response.MenuResponse;

import java.util.List;
import java.util.UUID;

public interface MenuService {
    List<MenuResponse> getAll(UUID tenantId, boolean includeInactive);
    MenuResponse create(UUID tenantId, MenuUpsertRequest req);
    MenuResponse update(UUID tenantId, UUID menuId, MenuUpsertRequest req);
    MenuResponse setActive(UUID tenantId, UUID menuId, boolean active);
}
