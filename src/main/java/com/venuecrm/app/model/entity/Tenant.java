package com.venuecrm.app.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tenant")
@Getter
@Setter
public class Tenant extends BaseModel {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(nullable = false, length = 40)
    private String vertical = "WEDDING_VENUE";

    @Column(name = "wa_phone_number_id", nullable = false, unique = true, length = 40)
    private String waPhoneNumberId;

    @Column(name = "wa_display_number", nullable = false, length = 20)
    private String waDisplayNumber;

    @Column(name = "wa_access_token", columnDefinition = "TEXT")
    private String waAccessToken;

    @Column(nullable = false, length = 40)
    private String timezone = "Europe/Istanbul";

    @Column(name = "greeting_text", columnDefinition = "text")
    private String greetingText;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "subscription_status", nullable = false, length = 20)
    private String subscriptionStatus = "TRIAL";

    @Column(name = "subscription_until")
    private LocalDate subscriptionUntil;

    @Column(name = "plan", nullable = false, length = 20)
    private String plan = "BASIC";

    @Column(name = "show_prices", nullable = false)
    private boolean showPrices = true;
}
