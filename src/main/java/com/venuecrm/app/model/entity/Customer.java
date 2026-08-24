package com.venuecrm.app.model.entity;

import com.venuecrm.app.whatsapp.WaTarget;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer extends BaseModel {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "wa_number", length = 20)
    private String waNumber;

    @Column(name = "wa_user_id", length = 64)
    private String waUserId;

    @Column(name = "full_name", length = 120)
    private String fullName;

    @Column(name = "is_blocked", nullable = false)
    private boolean blocked = false;

    /** Mesaj gonderirken kullanilacak hedef: telefon varsa telefon, yoksa BSUID. */
    public WaTarget toWaTarget() {
        return new WaTarget(waNumber, waUserId);
    }
}
