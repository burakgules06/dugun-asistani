package com.venuecrm.app.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "hall")
@Getter
@Setter
public class Hall extends BaseModel {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "capacity_min")
    private Integer capacityMin;

    @Column(name = "capacity_max")
    private Integer capacityMax;

    @Column(name = "sort_order", nullable = false)
    private short sortOrder = 0;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // 1 = günde tek düğün (yalnızca akşam), 2 = günde iki düğün (öğlen + akşam).
    @Column(name = "daily_capacity", nullable = false)
    private short dailyCapacity = 1;
}
