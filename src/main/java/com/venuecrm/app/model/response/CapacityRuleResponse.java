package com.venuecrm.app.model.response;

import java.util.List;

public record CapacityRuleResponse(
        String id,
        String hallId,
        String hallName,
        String menuId,
        String menuName,
        List<Integer> months,
        String timeSlot,
        boolean active,
        String note
) {}
