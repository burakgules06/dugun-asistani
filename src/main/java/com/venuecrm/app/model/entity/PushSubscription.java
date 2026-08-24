package com.venuecrm.app.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "push_subscription")
@Getter
@Setter
public class PushSubscription extends BaseModel {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "panel_user_id", nullable = false)
    private PanelUser panelUser;

    @Column(nullable = false, length = 500, unique = true)
    private String endpoint;

    @Column(name = "p256dh_key", nullable = false, length = 200)
    private String p256dhKey;

    @Column(name = "auth_key", nullable = false, length = 100)
    private String authKey;
}
