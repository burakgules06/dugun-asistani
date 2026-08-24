package com.venuecrm.app.model.entity;

import com.venuecrm.app.model.enums.LeadMood;
import com.venuecrm.app.model.enums.LeadSource;
import com.venuecrm.app.model.enums.TimeSlot;
import com.venuecrm.app.model.enums.LeadStage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Satış talebi kartı: WhatsApp botundan ya da elle oluşturulan bir düğün teklifi talebi. */
@Entity
@Table(name = "lead")
@Getter
@Setter
public class Lead extends BaseModel {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    // Kesin/onaylanmis dugun gunu - musteri WhatsApp'tan vermez, isletme panelden
    // gorusme sonrasi belirleyip buraya yazar. Takvim ekrani yalnizca bu alanı dolu
    // olan lead'leri gosterir.
    @Column(name = "event_date")
    private LocalDate eventDate;

    // Musterinin WhatsApp botunda belirttigi yaklasik donem (kesin tarih degil).
    @Column(name = "preferred_month")
    private LocalDate preferredMonth; // ayin ilk gunu olarak saklanir, orn. 2027-08-01

    @Column(name = "preferred_week")
    private Short preferredWeek; // 1-5: ayin kacinci haftasi

    // Musterinin tercih ettigi zaman dilimi: hafta ici aksam / hafta sonu aksam / hafta sonu gunduz.
    @Column(name = "preferred_time_slot", length = 20)
    @Enumerated(EnumType.STRING)
    private TimeSlot preferredTimeSlot;

    @Column(name = "guest_count_min")
    private Integer guestCountMin;

    @Column(name = "guest_count_max")
    private Integer guestCountMax; // null = ust siniri olmayan (acik uclu) aralik

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LeadSource source = LeadSource.MANUAL;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LeadStage stage = LeadStage.NEW;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private LeadMood mood;

    @Column(name = "price_given", nullable = false)
    private boolean priceGiven = false;

    @Column(name = "price_amount", precision = 10, scale = 2)
    private BigDecimal priceAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private PanelUser assignedUser;
}
