package com.venuecrm.app.model.response;

import java.time.OffsetDateTime;

public record LeadNoteResponse(
        String id,
        String authorName,
        String body,
        OffsetDateTime createdAt
) {}
