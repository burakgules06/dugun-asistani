package com.venuecrm.app.model.request;

public record PanelUserUpsertRequest(
        String phone,
        String password,
        String fullName,
        String role
) {}
