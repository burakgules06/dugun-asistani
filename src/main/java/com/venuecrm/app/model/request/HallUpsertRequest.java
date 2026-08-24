package com.venuecrm.app.model.request;

public record HallUpsertRequest(
        String name,
        String description,
        Integer capacityMin,
        Integer capacityMax,
        Integer sortOrder,
        Integer dailyCapacity
) {}
