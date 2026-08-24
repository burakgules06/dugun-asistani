package com.venuecrm.app.model.request;

import java.time.LocalDate;
import java.util.UUID;

public record LeadCreateRequest(
        String customerName,
        String customerPhone,
        UUID hallId,
        UUID menuId,
        LocalDate eventDate,
        Integer guestCountMin,
        Integer guestCountMax,
        String preferredTimeSlot
) {}
