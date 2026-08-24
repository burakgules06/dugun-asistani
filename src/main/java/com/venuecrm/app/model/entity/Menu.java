package com.venuecrm.app.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "menu")
@Getter
@Setter
public class Menu extends BaseModel {

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

    @Column(name = "price_per_person", precision = 10, scale = 2)
    private BigDecimal pricePerPerson;

    @Column(name = "sort_order", nullable = false)
    private short sortOrder = 0;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
