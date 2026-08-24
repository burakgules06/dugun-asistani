package com.venuecrm.app.service;

import com.venuecrm.app.model.entity.Customer;
import com.venuecrm.app.model.entity.Hall;
import com.venuecrm.app.model.entity.Lead;
import com.venuecrm.app.model.entity.Menu;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.enums.TimeSlot;
import com.venuecrm.app.model.request.LeadCreateRequest;
import com.venuecrm.app.model.request.LeadUpdateRequest;
import com.venuecrm.app.model.response.LeadResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LeadService {
    List<LeadResponse> getAll(UUID tenantId);
    LeadResponse getById(UUID tenantId, UUID leadId);
    LeadResponse create(UUID tenantId, LeadCreateRequest req);
    LeadResponse update(UUID tenantId, UUID leadId, LeadUpdateRequest req);

    /**
     * WhatsApp botu tarafından bir talep akışı tamamlandığında çağrılır. Müşteri kesin bir
     * tarih vermez; yalnızca tercih ettiği ay/hafta/gün tipini bildirir. Kesin tarihi (eventDate)
     * işletme daha sonra panelden belirler.
     */
    Lead createFromBot(Tenant tenant, Customer customer, Hall hall, Menu menu,
                        LocalDate preferredMonth, Integer preferredWeek, TimeSlot preferredTimeSlot,
                        Integer guestCountMin, Integer guestCountMax);
}
