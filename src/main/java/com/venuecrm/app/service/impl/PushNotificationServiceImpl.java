package com.venuecrm.app.service.impl;

import com.venuecrm.app.config.PushProperties;
import com.venuecrm.app.model.entity.PanelUser;
import com.venuecrm.app.model.entity.PushSubscription;
import com.venuecrm.app.model.request.PushSubscribeRequest;
import com.venuecrm.app.repository.PanelUserRepository;
import com.venuecrm.app.repository.PushSubscriptionRepository;
import com.venuecrm.app.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationServiceImpl.class);

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final PanelUserRepository panelUserRepository;
    private final PushProperties pushProperties;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Override
    @Transactional
    public void subscribe(UUID panelUserId, PushSubscribeRequest req) {
        PanelUser user = panelUserRepository.findById(panelUserId).orElseThrow();
        PushSubscription sub = pushSubscriptionRepository.findByEndpoint(req.endpoint()).orElseGet(PushSubscription::new);
        sub.setPanelUser(user);
        sub.setEndpoint(req.endpoint());
        sub.setP256dhKey(req.keys().p256dh());
        sub.setAuthKey(req.keys().auth());
        pushSubscriptionRepository.save(sub);
    }

    @Override
    @Transactional
    public void unsubscribe(String endpoint) {
        pushSubscriptionRepository.deleteByEndpoint(endpoint);
    }

    @Override
    @Transactional(readOnly = true)
    public void notifyTenantStaff(UUID tenantId, String title, String body) {
        if (pushProperties.publicKey() == null || pushProperties.publicKey().isBlank()) {
            log.debug("Push VAPID anahtarları tanımlı değil, bildirim atlandı");
            return;
        }
        var subs = pushSubscriptionRepository.findByPanelUser_Tenant_Id(tenantId);
        if (subs.isEmpty()) return;

        try {
            PushService pushService = new PushService(pushProperties.publicKey(), pushProperties.privateKey(), pushProperties.subject());
            String payload = "{\"title\":\"%s\",\"body\":\"%s\"}".formatted(escape(title), escape(body));

            for (PushSubscription sub : subs) {
                try {
                    var subscription = new nl.martijndwars.webpush.Subscription(
                            sub.getEndpoint(),
                            new nl.martijndwars.webpush.Subscription.Keys(sub.getP256dhKey(), sub.getAuthKey()));
                    pushService.send(new Notification(subscription, payload));
                } catch (Exception e) {
                    log.warn("Push bildirimi gönderilemedi (endpoint={}): {}", sub.getEndpoint(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Push servisi başlatılamadı: {}", e.getMessage());
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "'");
    }
}
