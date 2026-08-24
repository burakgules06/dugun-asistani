package com.venuecrm.app.controller;

import com.venuecrm.app.model.entity.PanelUser;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.repository.PanelUserRepository;
import com.venuecrm.app.security.JwtService;
import com.venuecrm.app.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final PanelUserRepository panelUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public record LoginRequest(String phone, String password) {}

    @PostMapping("/login")
    @Transactional(readOnly = true)
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String normalized = PhoneUtil.normalize(req.phone());
        PanelUser user = panelUserRepository
                .findByPhoneAndActiveTrue(normalized != null ? normalized : req.phone())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            log.warn("Başarısız giriş denemesi: telefon={}", PhoneUtil.mask(req.phone()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Telefon veya şifre hatalı"));
        }

        Tenant tenant = user.getTenant();
        UUID tenantId = tenant != null ? tenant.getId() : null;

        String token = jwtService.generateToken(user.getId(), tenantId, user.getRole());

        log.info("Başarılı giriş: userId={}, tenantId={}, role={}", user.getId(), tenantId, user.getRole());

        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("userId", user.getId().toString());
        body.put("fullName", user.getFullName() != null ? user.getFullName() : "");
        body.put("role", user.getRole());
        body.put("tenantName", tenant != null ? tenant.getDisplayName() : "");
        body.put("subscriptionStatus", tenant != null ? tenant.getSubscriptionStatus() : "");
        body.put("subscriptionUntil", tenant != null && tenant.getSubscriptionUntil() != null
                ? tenant.getSubscriptionUntil().toString() : "");

        return ResponseEntity.ok(body);
    }
}
