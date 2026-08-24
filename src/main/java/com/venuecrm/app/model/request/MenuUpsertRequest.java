package com.venuecrm.app.model.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record MenuUpsertRequest(
        String name,
        String description,
        BigDecimal pricePerPerson,
        Integer sortOrder,
        List<UUID> hallIds
) {}
