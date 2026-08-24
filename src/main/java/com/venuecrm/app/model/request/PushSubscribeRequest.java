package com.venuecrm.app.model.request;

/** Tarayıcının PushSubscription.toJSON() çıktısıyla birebir eşleşir. */
public record PushSubscribeRequest(String endpoint, Keys keys) {
    public record Keys(String p256dh, String auth) {}
}
