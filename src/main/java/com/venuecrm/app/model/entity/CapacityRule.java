package com.venuecrm.app.model.entity;

import com.venuecrm.app.model.enums.TimeSlot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Bir salon/menü kombinasyonunun hangi ay(lar) + zaman diliminde KAPALI sayilacagini
 * tanimlayan istisna kaydi. hall/menu/months/timeSlot null ise ilgili kapsam
 * tum salonlar/menuler/aylar/dilimler icin gecerlidir (wildcard).
 */
@Entity
@Table(name = "capacity_rule")
@Getter
@Setter
public class CapacityRule extends BaseModel {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    // Bos/null -> tum aylar icin gecerli
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "months", columnDefinition = "smallint[]")
    private short[] months;

    // null -> tum zaman dilimleri icin gecerli
    @Column(name = "time_slot", length = 20)
    @Enumerated(EnumType.STRING)
    private TimeSlot timeSlot;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(length = 255)
    private String note;
}
