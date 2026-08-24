package com.venuecrm.app.service.impl;

import com.venuecrm.app.exception.runtime.HallNotFoundException;
import com.venuecrm.app.exception.runtime.LeadNotBelongToTenantException;
import com.venuecrm.app.exception.runtime.LeadNotFoundException;
import com.venuecrm.app.exception.runtime.MenuNotFoundException;
import com.venuecrm.app.model.entity.*;
import com.venuecrm.app.model.enums.LeadMood;
import com.venuecrm.app.model.enums.LeadSource;
import com.venuecrm.app.model.enums.LeadStage;
import com.venuecrm.app.model.enums.TimeSlot;
import com.venuecrm.app.model.request.LeadCreateRequest;
import com.venuecrm.app.model.request.LeadUpdateRequest;
import com.venuecrm.app.model.response.LeadResponse;
import com.venuecrm.app.repository.*;
import com.venuecrm.app.service.LeadNoteService;
import com.venuecrm.app.service.LeadService;
import com.venuecrm.app.service.PushNotificationService;
import com.venuecrm.app.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadNoteRepository leadNoteRepository;
    private final CustomerRepository customerRepository;
    private final HallRepository hallRepository;
    private final MenuRepository menuRepository;
    private final PanelUserRepository panelUserRepository;
    private final TenantRepository tenantRepository;
    private final LeadNoteService leadNoteService;
    private final PushNotificationService pushNotificationService;

    @Override
    @Transactional(readOnly = true)
    public List<LeadResponse> getAll(UUID tenantId) {
        return leadRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LeadResponse getById(UUID tenantId, UUID leadId) {
        return toResponse(loadOwned(tenantId, leadId));
    }

    @Override
    @Transactional
    public LeadResponse create(UUID tenantId, LeadCreateRequest req) {
        if (req.customerName() == null || req.customerName().isBlank()) {
            throw new IllegalArgumentException("Müşteri adı boş olamaz");
        }
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();

        Customer customer = new Customer();
        customer.setTenant(tenant);
        customer.setFullName(req.customerName().trim());
        customer.setWaNumber(PhoneUtil.normalize(req.customerPhone()));
        customer = customerRepository.save(customer);

        Lead lead = new Lead();
        lead.setTenant(tenant);
        lead.setCustomer(customer);
        lead.setHall(resolveHall(tenantId, req.hallId()));
        lead.setMenu(resolveMenu(tenantId, req.menuId()));
        lead.setEventDate(req.eventDate());
        lead.setGuestCountMin(req.guestCountMin());
        lead.setGuestCountMax(req.guestCountMax());
        lead.setPreferredTimeSlot(parseTimeSlot(req.preferredTimeSlot()));
        lead.setSource(LeadSource.MANUAL);
        lead.setStage(LeadStage.NEW);

        Lead saved = leadRepository.saveAndFlush(lead);
        leadNoteService.addSystemNote(saved.getId(), "Talep panelden manuel olarak oluşturuldu.");
        return toResponse(saved);
    }

    @Override
    @Transactional
    public LeadResponse update(UUID tenantId, UUID leadId, LeadUpdateRequest req) {
        Lead lead = loadOwned(tenantId, leadId);

        if (req.hallId() != null) lead.setHall(resolveHall(tenantId, req.hallId()));
        if (req.menuId() != null) lead.setMenu(resolveMenu(tenantId, req.menuId()));
        if (req.eventDate() != null) lead.setEventDate(req.eventDate());
        if (req.guestCountMin() != null) lead.setGuestCountMin(req.guestCountMin());
        if (req.guestCountMax() != null) lead.setGuestCountMax(req.guestCountMax());
        if (req.preferredTimeSlot() != null) lead.setPreferredTimeSlot(parseTimeSlot(req.preferredTimeSlot()));
        if (req.stage() != null) lead.setStage(LeadStage.valueOf(req.stage()));
        if (req.mood() != null) lead.setMood(req.mood().isBlank() ? null : LeadMood.valueOf(req.mood()));
        if (req.priceGiven() != null) lead.setPriceGiven(req.priceGiven());
        if (req.priceAmount() != null) lead.setPriceAmount(req.priceAmount());
        if (req.assignedUserId() != null) {
            PanelUser assignee = panelUserRepository.findById(req.assignedUserId()).orElse(null);
            lead.setAssignedUser(assignee);
        }
        // @UpdateTimestamp flush aninda uygulanir - flush edilmezse yanittaki updatedAt
        // hala eski degeri gosterir.
        return toResponse(leadRepository.saveAndFlush(lead));
    }

    @Override
    @Transactional
    public Lead createFromBot(Tenant tenant, Customer customer, Hall hall, Menu menu,
                               LocalDate preferredMonth, Integer preferredWeek, TimeSlot preferredTimeSlot,
                               Integer guestCountMin, Integer guestCountMax) {
        Lead lead = new Lead();
        lead.setTenant(tenant);
        lead.setCustomer(customer);
        lead.setHall(hall);
        lead.setMenu(menu);
        lead.setPreferredMonth(preferredMonth);
        lead.setPreferredWeek(preferredWeek != null ? preferredWeek.shortValue() : null);
        lead.setPreferredTimeSlot(preferredTimeSlot);
        lead.setGuestCountMin(guestCountMin);
        lead.setGuestCountMax(guestCountMax);
        lead.setSource(LeadSource.WHATSAPP_BOT);
        lead.setStage(LeadStage.NEW);

        Lead saved = leadRepository.save(lead);
        leadNoteService.addSystemNote(saved.getId(), "Talep WhatsApp botu üzerinden alındı.");

        String customerLabel = customer.getFullName() != null ? customer.getFullName() : "Bir müşteri";
        pushNotificationService.notifyTenantStaff(tenant.getId(),
                "Yeni WhatsApp Talebi",
                customerLabel + " yeni bir teklif talebinde bulundu.");

        return saved;
    }

    private Hall resolveHall(UUID tenantId, UUID hallId) {
        if (hallId == null) return null;
        Hall hall = hallRepository.findById(hallId).orElseThrow(HallNotFoundException::new);
        if (!hall.getTenant().getId().equals(tenantId)) throw new HallNotFoundException();
        return hall;
    }

    private Menu resolveMenu(UUID tenantId, UUID menuId) {
        if (menuId == null) return null;
        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);
        if (!menu.getTenant().getId().equals(tenantId)) throw new MenuNotFoundException();
        return menu;
    }

    private TimeSlot parseTimeSlot(String timeSlot) {
        return (timeSlot == null || timeSlot.isBlank()) ? null : TimeSlot.valueOf(timeSlot);
    }

    private Lead loadOwned(UUID tenantId, UUID leadId) {
        Lead lead = leadRepository.findById(leadId).orElseThrow(LeadNotFoundException::new);
        if (!lead.getTenant().getId().equals(tenantId)) {
            throw new LeadNotBelongToTenantException();
        }
        return lead;
    }

    private LeadResponse toResponse(Lead l) {
        int noteCount = leadNoteRepository.findByLead_IdOrderByCreatedAtDesc(l.getId()).size();
        Customer c = l.getCustomer();
        Hall h = l.getHall();
        Menu m = l.getMenu();
        PanelUser assignee = l.getAssignedUser();
        return new LeadResponse(
                l.getId().toString(),
                c.getId().toString(), c.getFullName(), c.getWaNumber(),
                h != null ? h.getId().toString() : null, h != null ? h.getName() : null,
                m != null ? m.getId().toString() : null, m != null ? m.getName() : null,
                l.getEventDate(), l.getPreferredMonth(), l.getPreferredWeek() != null ? l.getPreferredWeek().intValue() : null,
                l.getGuestCountMin(), l.getGuestCountMax(),
                l.getPreferredTimeSlot() != null ? l.getPreferredTimeSlot().name() : null,
                l.getSource().name(), l.getStage().name(), l.getMood() != null ? l.getMood().name() : null,
                l.isPriceGiven(), l.getPriceAmount(),
                assignee != null ? assignee.getId().toString() : null,
                assignee != null ? assignee.getFullName() : null,
                noteCount, l.getCreatedAt(), l.getUpdatedAt()
        );
    }
}
