package com.venuecrm.app.model.request;

import java.util.List;
import java.util.UUID;

public record CapacityRuleUpsertRequest(
        UUID hallId,
        UUID menuId,
        List<Integer> months,
        String timeSlot,
        Boolean active,
        String note
) {}
