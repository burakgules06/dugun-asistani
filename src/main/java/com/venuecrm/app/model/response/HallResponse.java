package com.venuecrm.app.model.response;

public record HallResponse(
        String id,
        String name,
        String description,
        Integer capacityMin,
        Integer capacityMax,
        int sortOrder,
        boolean active,
        int dailyCapacity
) {}
