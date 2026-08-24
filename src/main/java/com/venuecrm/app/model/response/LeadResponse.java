package com.venuecrm.app.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record LeadResponse(
        String id,
        String customerId,
        String customerName,
        String customerPhone,
        String hallId,
        String hallName,
        String menuId,
        String menuName,
        LocalDate eventDate,
        LocalDate preferredMonth,
        Integer preferredWeek,
        Integer guestCountMin,
        Integer guestCountMax,
        String preferredTimeSlot,
        String source,
        String stage,
        String mood,
        boolean priceGiven,
        BigDecimal priceAmount,
        String assignedUserId,
        String assignedUserName,
        int noteCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
