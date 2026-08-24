package com.venuecrm.app.service;

import com.venuecrm.app.model.request.PushSubscribeRequest;

import java.util.UUID;

public interface PushNotificationService {
    void subscribe(UUID panelUserId, PushSubscribeRequest req);
    void unsubscribe(String endpoint);
    /** Bir tenant'a bağlı tüm panel kullanıcılarının aktif push aboneliklerine bildirim gönderir. */
    void notifyTenantStaff(UUID tenantId, String title, String body);
}
