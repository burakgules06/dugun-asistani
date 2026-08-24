package com.venuecrm.app.service;

import com.venuecrm.app.model.request.ChangePasswordRequest;
import com.venuecrm.app.model.request.PanelUserUpsertRequest;
import com.venuecrm.app.model.response.PanelUserResponse;

import java.util.List;
import java.util.UUID;

public interface PanelUserService {
    List<PanelUserResponse> getAll(UUID tenantId);
    PanelUserResponse create(UUID tenantId, PanelUserUpsertRequest req);
    PanelUserResponse setActive(UUID tenantId, UUID userId, boolean active);
    void changePassword(UUID userId, ChangePasswordRequest req);
}
