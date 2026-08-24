package com.venuecrm.app.service;

import com.venuecrm.app.model.request.CapacityRuleUpsertRequest;
import com.venuecrm.app.model.response.CapacityRuleResponse;

import java.util.List;
import java.util.UUID;

public interface CapacityRuleService {
    List<CapacityRuleResponse> getAll(UUID tenantId);
    CapacityRuleResponse create(UUID tenantId, CapacityRuleUpsertRequest req);
    CapacityRuleResponse update(UUID tenantId, UUID ruleId, CapacityRuleUpsertRequest req);
    void delete(UUID tenantId, UUID ruleId);
}
