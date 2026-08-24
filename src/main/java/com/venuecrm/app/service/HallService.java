package com.venuecrm.app.service;

import com.venuecrm.app.model.request.HallUpsertRequest;
import com.venuecrm.app.model.response.HallResponse;

import java.util.List;
import java.util.UUID;

public interface HallService {
    List<HallResponse> getAll(UUID tenantId, boolean includeInactive);
    HallResponse create(UUID tenantId, HallUpsertRequest req);
    HallResponse update(UUID tenantId, UUID hallId, HallUpsertRequest req);
    HallResponse setActive(UUID tenantId, UUID hallId, boolean active);
    void reorder(UUID tenantId, List<UUID> orderedIds);
}
