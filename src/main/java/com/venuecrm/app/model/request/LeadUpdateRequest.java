package com.venuecrm.app.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LeadUpdateRequest(
        UUID hallId,
        UUID menuId,
        LocalDate eventDate,
        Integer guestCountMin,
        Integer guestCountMax,
        String preferredTimeSlot,
        String stage,
        String mood,
        Boolean priceGiven,
        BigDecimal priceAmount,
        UUID assignedUserId
) {}
