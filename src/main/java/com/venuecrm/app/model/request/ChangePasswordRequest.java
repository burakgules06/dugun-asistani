package com.venuecrm.app.model.request;

public record ChangePasswordRequest(String currentPassword, String newPassword) {}
