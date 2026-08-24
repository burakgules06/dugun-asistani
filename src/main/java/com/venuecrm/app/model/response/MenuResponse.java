package com.venuecrm.app.model.response;

import java.math.BigDecimal;
import java.util.List;

public record MenuResponse(
        String id,
        String name,
        String description,
        BigDecimal pricePerPerson,
        int sortOrder,
        boolean active,
        List<String> hallIds
) {}
